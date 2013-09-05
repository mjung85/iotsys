/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package at.ac.tuwien.auto.calimero.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.KNXAddress;
import at.ac.tuwien.auto.calimero.buffer.Configuration.NetworkFilter;
import at.ac.tuwien.auto.calimero.buffer.Configuration.RequestFilter;
import at.ac.tuwien.auto.calimero.buffer.cache.Cache;
import at.ac.tuwien.auto.calimero.buffer.cache.CacheObject;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.cemi.CEMIFactory;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.datapoint.Datapoint;
import at.ac.tuwien.auto.calimero.datapoint.DatapointMap;
import at.ac.tuwien.auto.calimero.datapoint.DatapointModel;
import at.ac.tuwien.auto.calimero.datapoint.StateDP;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;


/**
 * Predefined filter for filtering KNX messages of datapoints with state semantic into the
 * associated network buffer configuration.
 * <p>
 * This filter might be used in a configuration to build up and maintain a process image
 * of the KNX network the used network link communicates with. The buffer will keep the
 * most up to date state to a KNX group address / datapoint.<br>
 * KNX messages are buffered using a {@link LDataObject} (an object of this
 * type is also expected when the request method is invoked).
 * <p>
 * If a datapoint model is available in the {@link Configuration}, the filter uses that
 * model in its {@link #init(Configuration)} method. It initializes its local lookup
 * references with necessary updating / invalidating information of other datapoints
 * stored in that model. Thus, the filter will update or invalidate all other associated
 * datapoint state values in the network buffer configuration when receiving a new KNX
 * message.<br>
 * To reflect subsequent changes of the datapoint model in the filter, the filter has to
 * be reinitialized (using {@link #init(Configuration)}.
 * 
 * @author B. Malinowsky
 */
public class StateFilter implements NetworkFilter, RequestFilter
{
	// TODO: provide an automated way to detect and react
	// on changes in the datapoint model
	
	// contains cross references of datapoints: which datapoint (key, of
	// type KNXAddress) invalidates/updates which datapoints (value,
	// of type List with GroupAddress entries)
	private Map invalidate;
	private Map update;
	
	/**
	 * Creates a new state based filter.
	 * <p>
	 */
	public StateFilter()
	{}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.Configuration.NetworkFilter#init
	 * (tuwien.auto.calimero.buffer.Configuration)
	 */
	public void init(Configuration c)
	{
		final DatapointModel m = c.getDatapointModel();
		if (m != null)
			createReferences(m);
	}
	
	/**
	 * Applies state based filter rules on frame.
	 * <p>
	 * Criteria for accept:
	 * <ul>
	 * <li>the KNX message destination address is a group address</li>
	 * <li>there is <b>no</b> datapoint model available in the configuration, or</li>
	 * <li>there is a datapoint model available with a datapoint identified by the
	 * destination address <b>and</b> the datapoint is state based</li>
	 * <li>the message is an application layer group write or group response</li>
	 * </ul>
	 * On acceptance, the frame is stored into the configuration cache using a
	 * {@link LDataObject}. For easier handling of subsequent read requests on such a
	 * buffered frame, all frames are converted to L-data indications with application
	 * layer group response service code before getting stored.
	 * <p>
	 * If update and invalidation information is available, other dependent datapoint
	 * state values will be updated or invalidated appropriately.
	 * 
	 * @param frame {@inheritDoc}
	 * @param c {@inheritDoc}
	 */
	public void accept(CEMI frame, Configuration c)
	{
		final Cache cache = c.getCache();
		if (cache == null || !(frame instanceof CEMILData))
			return;
	
		final CEMILData f = (CEMILData) frame;
		if (!(f.getDestination() instanceof GroupAddress))
			return;
		final GroupAddress dst = (GroupAddress) f.getDestination();
		final DatapointModel m = c.getDatapointModel();
		Datapoint dp = null;
		if (m != null && ((dp = m.get(dst)) == null || !dp.isStateBased()))
			return;
		final byte[] d = f.getPayload();
		// filter for A-Group write (0x80) and read.res (0x40) services
		final int svc = d[0] & 0x03 | d[1] & 0xC0;
		CEMILData copy;
		if (svc == 0x40)
			// actually, read.res could be in a L-Data.con, too... ignore for now
			copy = f;
		else if (svc == 0x80) {
			// adjust to response frame
			d[1] = (byte) (d[1] & 0x3f | 0x40);
			try {
				copy = (CEMILData) CEMIFactory.create(CEMILData.MC_LDATA_IND, d, f);
			}
			catch (final KNXFormatException e) {
				NetworkBuffer.logger.error("preparing message for buffer failed", e);
				return;
			}
		}
		else
			return;
		// put into cache object
		final CacheObject co = cache.get(dst);
		if (co != null) {
			((LDataObject) co).setFrame(copy);
			c.getCache().put(co);
		}
		else
			cache.put(new LDataObject(copy));
		
		// do invalidation/update of other datapoints
		// a write updates and invalidates, read.res only updates
		update(copy, cache);
		if (svc == 0x80)
			invalidate(copy, cache);
	}
	
	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.buffer.Configuration.RequestFilter#request(
	 * tuwien.auto.calimero.KNXAddress, tuwien.auto.calimero.buffer.Configuration)
	 */
	public CEMILData request(KNXAddress dst, Configuration c)
	{
		final Cache cache = c.getCache();
		if (cache == null || !(dst instanceof GroupAddress))
			return null;
		final LDataObject o = (LDataObject) cache.get(dst);
		if (o == null)
			return null;
		// check if there is an expiration timeout for a state based value
		final Datapoint dp;
		final DatapointModel m = c.getDatapointModel();
		if (m != null && (dp = m.get((GroupAddress) dst)) != null && dp.isStateBased()) {
			final int t = ((StateDP) dp).getExpirationTimeout() * 1000;
			if (t != 0 && System.currentTimeMillis() > o.getTimestamp() + t)
				return null;
		}
		return o.getFrame();
	}
	
	private void update(CEMILData f, Cache c)
	{
		if (update != null) {
			final List upd = (List) update.get(f.getDestination());
			if (upd != null)
				for (final Iterator i = upd.iterator(); i.hasNext();) {
					final CacheObject co = c.get(i.next());
					if (co != null)
						((LDataObject) co).setFrame((CEMILData) CEMIFactory.create(null,
							(KNXAddress) co.getKey(), f, false));
				}
		}
	}
	
	private void invalidate(CEMILData f, Cache c)
	{
		if (invalidate != null) {
			final List inv = (List) invalidate.get(f.getDestination());
			if (inv != null)
				for (final Iterator i = inv.iterator(); i.hasNext();)
					c.remove(i.next());
		}
	}
	
	private void createReferences(DatapointModel model)
	{
		invalidate = new HashMap();
		update = new HashMap();
		final Collection c = ((DatapointMap) model).getDatapoints();
		synchronized (c) {
			for (final Iterator i = c.iterator(); i.hasNext();) {
				try {
					final StateDP dp = (StateDP) i.next();
					createReferences(invalidate, dp.getAddresses(false), dp
						.getMainAddress());
					createReferences(update, dp.getAddresses(true), dp.getMainAddress());
				}
				catch (final ClassCastException ignore) {}
			}
		}
	}

	private void createReferences(Map map, Collection forAddr, GroupAddress toAddr)
	{
		for (final Iterator i = forAddr.iterator(); i.hasNext();) {
			final Object o = i.next();
			List l = (List) map.get(o);
			if (l == null)
				map.put(o, l = new ArrayList());
			l.add(toAddr);
		}
	}
}
