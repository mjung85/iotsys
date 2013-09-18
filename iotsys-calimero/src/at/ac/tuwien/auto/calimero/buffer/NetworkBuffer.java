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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.KNXAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.buffer.cache.Cache;
import at.ac.tuwien.auto.calimero.buffer.cache.CacheObject;
import at.ac.tuwien.auto.calimero.buffer.cache.LFUCache;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.datapoint.DatapointModel;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.link.KNXLinkClosedException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLink;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.link.medium.KNXMediumSettings;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * A network buffer temporarily stores KNX network messages.
 * <p>
 * Reasons to do this might be to lower the response time when answering frequently
 * occurring application queries, leading to a better runtime performance. Another use
 * would be to enable user polled applications.
 * <p>
 * A network buffer contains one or more {@link Configuration}s, each of it maintains a
 * setting how to handle, i.e. filter and buffer, certain messages. In other words, a
 * network buffer obtains knowledge of buffering messages by adding configurations.<br>
 * A configuration works with filters set by the user for that configuration. A filter -
 * depending on its filter type - either applies individual filter rules on incoming
 * messages ({@link Configuration.NetworkFilter}) or answers requests from users or
 * components working with that configuration ({@link Configuration.RequestFilter}).<br>
 * If a created configuration is activated and no network filter is set, a default filter
 * is used, which simply accepts all cEMI L-Data.<br>
 * If no request filter is set, no buffer lookup is done on requests, instead the request
 * is forwarded directly to the KNX network.
 * <p>
 * In general, one network buffer is created for one KNX installation, to easier
 * distinguish between different installations. Nevertheless, this is not enforced in any
 * way; a new configuration also might just always use a new network buffer.
 * 
 * @author B. Malinowsky
 */
public final class NetworkBuffer
{
	// this is for network link only, for now
	private static final class ConfigImpl implements Configuration
	{
		private final SquirrelLink lnk;
		private final NetworkLinkListener ll;
		private Cache cache;
		private Configuration.NetworkFilter nwFilter;
		private Configuration.RequestFilter reqFilter;
		private volatile boolean active;
		private volatile boolean queryBufferOnly;
		private DatapointModel model;

		private final class SquirrelListener implements NetworkLinkListener
		{
			SquirrelListener()
			{}

			public void confirmation(FrameEvent e)
			{
				if (active && ((CEMILData) e.getFrame()).isPositiveConfirmation())
					updateBuffer(e.getFrame());
			}

			public void indication(FrameEvent e)
			{
				if (active)
					updateBuffer(e.getFrame());
			}

			public void linkClosed(CloseEvent e)
			{
				activate(false);
			}

			private void updateBuffer(CEMI frame)
			{
				if (nwFilter != null)
					nwFilter.accept(frame, ConfigImpl.this);
			}
		}

		private final class SquirrelLink implements KNXNetworkLink
		{
			private final KNXNetworkLink base;
			private final List listeners = new Vector();

			SquirrelLink(KNXNetworkLink baseLink)
			{
				base = baseLink;
			}

			public void addLinkListener(NetworkLinkListener l)
			{
				base.addLinkListener(l);
				listeners.add(l);
			}

			public void close()
			{
				activate(false);
				base.close();
				listeners.clear();
			}

			public byte getHopCount()
			{
				return base.getHopCount();
			}

			public KNXMediumSettings getKNXMedium()
			{
				return base.getKNXMedium();
			}

			public void removeLinkListener(NetworkLinkListener l)
			{
				base.removeLinkListener(l);
				listeners.remove(l);
			}

			public void sendRequest(KNXAddress dst, Priority p, byte[] nsdu)
				throws KNXLinkClosedException, KNXTimeoutException
			{
				if (!doBufferedResponse(dst, nsdu))
					base.sendRequest(dst, p, nsdu);
			}

			public void sendRequestWait(KNXAddress dst, Priority p, byte[] nsdu)
				throws KNXTimeoutException, KNXLinkClosedException
			{
				if (!doBufferedResponse(dst, nsdu))
					base.sendRequestWait(dst, p, nsdu);
			}

			public void send(CEMILData msg, boolean waitForCon)
				throws KNXTimeoutException, KNXLinkClosedException
			{
				if (!doBufferedResponse(msg.getDestination(), msg.getPayload()))
					base.send(msg, waitForCon);
			}

			public void setHopCount(int count)
			{
				base.setHopCount(count);
			}

			public void setKNXMedium(KNXMediumSettings a)
			{
				base.setKNXMedium(a);
			}

			public String getName()
			{
				return "buffered " + base.getName();
			}

			public boolean isOpen()
			{
				return base.isOpen();
			}

			private boolean doBufferedResponse(KNXAddress dst, byte[] nsdu)
				throws KNXTimeoutException
			{
				final RequestFilter rf = reqFilter;
				// check valid access and A-group.read
				if (rf == null || !isOpen() || DataUnitBuilder.getAPDUService(nsdu) != 0)
					return false;
				final CEMILData cemi = rf.request(dst, ConfigImpl.this);
				if (cemi != null) {
					fireIndication(cemi);
					return true;
				}
				if (queryBufferOnly)
					throw new KNXTimeoutException("query limited to network buffer");
				return false;
			}

			private void fireIndication(CEMILData frame)
			{
				final FrameEvent e = new FrameEvent(this, frame);
				synchronized (listeners) {
					for (final Iterator i = listeners.iterator(); i.hasNext();) {
						final NetworkLinkListener l = (NetworkLinkListener) i.next();
						try {
							l.indication(e);
						}
						catch (final RuntimeException rte) {
							removeLinkListener(l);
						}
					}
				}
			}
		}

		ConfigImpl(KNXNetworkLink link)
		{
			lnk = new SquirrelLink(link);
			ll = new SquirrelListener();
			link.addLinkListener(ll);
		}

		public void activate(boolean activate)
		{
			active = activate;
			if (active && getCache() == null)
				setCache(new LFUCache(0, 0));
			// supply a really simple "all you can buffer"-filter
			if (active && nwFilter == null)
				nwFilter = new NetworkFilter()
				{
					public void accept(CEMI frame, Configuration c)
					{
						final Cache localCache = c.getCache();
						if (localCache == null || !(frame instanceof CEMILData))
							return;
						final CEMILData f = (CEMILData) frame;
						// put into cache object
						final CacheObject co = localCache.get(f.getDestination());
						if (co != null) {
							((LDataObject) co).setFrame(f);
							c.getCache().put(co);
						}
						else
							localCache.put(new LDataObject(f));
					}

					public void init(Configuration c)
					{}
				};
		}

		public boolean isActive()
		{
			return active;
		}

		public KNXNetworkLink getBaseLink()
		{
			return lnk.base;
		}

		public KNXNetworkLink getBufferedLink()
		{
			return lnk;
		}

		public void setQueryBufferOnly(boolean bufferOnly)
		{
			queryBufferOnly = bufferOnly;
		}
		
		public synchronized void setCache(Cache c)
		{
			if (c == null && active)
				activate(false);
			cache = c;
		}

		public synchronized Cache getCache()
		{
			return cache;
		}

		public synchronized void setDatapointModel(DatapointModel m)
		{
			model = m;
		}

		public synchronized DatapointModel getDatapointModel()
		{
			return model;
		}

		public void setFilter(NetworkFilter nf, RequestFilter rf)
		{
			nwFilter = nf;
			reqFilter = rf;
			if (nwFilter != null)
				nwFilter.init(this);
		}

		public NetworkFilter getNetworkFilter()
		{
			return nwFilter;
		}

		public RequestFilter getRequestFilter()
		{
			return reqFilter;
		}

		void unregister()
		{
			activate(false);
			getBaseLink().removeLinkListener(ll);
		}
	}

	/** Name of the log service used for network buffer logging. */
	public static final String LOG_SERVICE = "network buffer";
	
	static final LogService logger = LogManager.getManager().getLogService(LOG_SERVICE);

	// all network buffers currently in use
	private static final List buffers = new ArrayList();
	private static int uniqueInstID;

	private final List configs = Collections.synchronizedList(new ArrayList());
	private final String inst;

	private NetworkBuffer(String installation)
	{
		inst = installation;
	}

	/**
	 * Creates a new network buffer for a KNX installation.
	 * <p>
	 * To identify the buffer an unique installation identifier can be given through
	 * <code>installationID</code>.<br>
	 * If <code>null</code> or an empty string is supplied for the installation ID, a
	 * new default ID is generated of the form "Installation [ID]", where [ID] is an
	 * unique incrementing number. Note, the installation ID string is treated case
	 * sensitive.
	 * 
	 * @param installationID installation identifier for the network buffer, or
	 *        <code>null</code>
	 * @return the new network buffer
	 */
	public static synchronized NetworkBuffer createBuffer(String installationID)
	{
		if (getBuffer(installationID) != null)
			throw new KNXIllegalArgumentException("buffer \"" + installationID
				+ "\" already exists");
		final NetworkBuffer b = new NetworkBuffer(validateInstID(installationID));
		buffers.add(b);
		logger.info("created network buffer \"" + installationID + "\"");
		return b;
	}

	/**
	 * Removes a network buffer, and all configurations of that buffer.
	 * <p>
	 * For every {@link Configuration} contained in the buffer,
	 * {@link NetworkBuffer#removeConfiguration
	 * (at.ac.tuwien.auto.calimero.buffer.Configuration)} is called.
	 * 
	 * @param installationID installation ID of the network buffer to remove
	 */
	public static synchronized void removeBuffer(String installationID)
	{
		for (final Iterator i = buffers.iterator(); i.hasNext();) {
			final NetworkBuffer b = (NetworkBuffer) i.next();
			if (b.inst.equals(installationID)) {
				i.remove();
				while (!b.configs.isEmpty())
					b.removeConfiguration((Configuration) b.configs
						.get(b.configs.size() - 1));
				logger.info("removed network buffer \"" + installationID + "\"");
				return;
			}
		}
	}

	/**
	 * Returns the network buffer for the given installation ID.
	 * <p>
	 * 
	 * @param installationID installation ID for the network buffer
	 * @return the network buffer, or <code>null</code> if no buffer found
	 */
	public static synchronized NetworkBuffer getBuffer(String installationID)
	{
		for (final Iterator i = buffers.iterator(); i.hasNext();) {
			final NetworkBuffer db = (NetworkBuffer) i.next();
			if (db.inst.equals(installationID))
				return db;
		}
		return null;
	}

	/**
	 * Returns all network buffers currently in use.
	 * <p>
	 * 
	 * @return array of network buffers, array length is 0 on no network buffers
	 */
	public static synchronized NetworkBuffer[] getAllBuffers()
	{
		return (NetworkBuffer[]) buffers.toArray(new NetworkBuffer[buffers.size()]);
	}

	/**
	 * Creates a new configuration for the network buffer identified by the installation
	 * ID.
	 * <p>
	 * The configuration is added to the network buffer specified by the installation ID.
	 * If a network buffer with the supplied installation ID does not exist, it will be
	 * created. If <code>null</code> or an empty string is supplied for the installation
	 * ID, a new default ID is generated (see {@link NetworkBuffer#createBuffer(String)}.
	 * <br>
	 * If the supplied <code>link</code> gets closed, the created configuration will get
	 * deactivated (see {@link Configuration#activate(boolean)}), and the buffered link
	 * of the configuration, obtained with {@link Configuration#getBufferedLink()}, will
	 * get closed as well.
	 * 
	 * @param link KNX network link communicating with the KNX network
	 * @param installationID installation identifier for the network buffer, or
	 *        <code>null</code>
	 * @return the new configuration
	 */
	public static Configuration createConfiguration(KNXNetworkLink link,
		String installationID)
	{
		NetworkBuffer b = getBuffer(installationID);
		if (b == null)
			b = createBuffer(installationID);
		return b.createConfiguration(link);
	}

	/**
	 * Removes a configuration from the network buffer identified by the installation ID.
	 * <p>
	 * If <code>installationID</code> is <code>null</code>, all network buffers are
	 * searched for the configuration <code>c</code>.<br>
	 * If the network buffer is found containing the specified configuration,
	 * {@link NetworkBuffer#removeConfiguration
	 * (at.ac.tuwien.auto.calimero.buffer.Configuration)} is called on that buffer.
	 * 
	 * @param c the configuration to remove
	 * @param installationID installation identifier for the network buffer, or
	 *        <code>null</code>
	 */
	public static synchronized void removeConfiguration(Configuration c,
		String installationID)
	{
		for (final Iterator i = buffers.iterator(); i.hasNext();) {
			final NetworkBuffer b = (NetworkBuffer) i.next();
			if (b.inst.equals(installationID) || installationID == null
				&& b.configs.contains(c)) {
				b.removeConfiguration(c);
				return;
			}
		}
	}

	/**
	 * Creates a new configuration for this network buffer.
	 * <p>
	 * If the supplied <code>link</code> gets closed, the created configuration will get
	 * deactivated (see {@link Configuration#activate(boolean)}), and the buffered link
	 * of the configuration, obtained with {@link Configuration#getBufferedLink()}, will
	 * get closed as well.
	 * 
	 * @param link KNX network link communicating with the KNX network
	 * @return the new configuration
	 */
	public Configuration createConfiguration(KNXNetworkLink link)
	{
		final ConfigImpl c = new ConfigImpl(link);
		configs.add(c);
		logger.info("created configuration for " + link.getName());
		return c;
	}

	/**
	 * Removes a configuration from this network buffer.
	 * <p>
	 * The configuration is deactivated and will not receive any further events or
	 * incoming messages from the base network link supplied at creation of that
	 * configuration.
	 * 
	 * @param c the configuration to remove
	 */
	public void removeConfiguration(Configuration c)
	{
		if (configs.remove(c)) {
			((ConfigImpl) c).unregister();
			logger.info("removed configuration of " + c.getBaseLink().getName());
		}
	}

	/**
	 * Gets the configuration which provides the buffered link.
	 * <p>
	 * If the network link is not a buffered link or not found in the current
	 * configurations of this network buffer, <code>null</code> is returned.
	 * 
	 * @param bufferedLink the buffered link to get the configuration for
	 * @return the owning configuration of that link or <code>null</code>
	 */
	public Configuration getConfiguration(KNXNetworkLink bufferedLink)
	{
		synchronized (configs) {
			for (final Iterator i = configs.iterator(); i.hasNext();) {
				final ConfigImpl lc = (ConfigImpl) i.next();
				if (lc.getBufferedLink() == bufferedLink)
					return lc;
			}
		}
		return null;
	}

	/**
	 * Returns the installation identifier of this network buffer.
	 * <p>
	 * 
	 * @return installation ID
	 */
	public String getInstallationID()
	{
		return inst;
	}

	private static String validateInstID(String instID)
	{
		if (instID == null || instID.length() == 0)
			return "Installation " + ++uniqueInstID;
		return instID;
	}
}
