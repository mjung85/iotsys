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

package at.ac.tuwien.auto.calimero.process;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.DetachEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.datapoint.Datapoint;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator8BitUnsigned;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorString;
import at.ac.tuwien.auto.calimero.dptxlator.TranslatorTypes;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXInvalidResponseException;
import at.ac.tuwien.auto.calimero.exception.KNXRemoteException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.link.KNXLinkClosedException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLink;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * This implementation of the process communicator uses in any case the DPT translators
 * {@link DPTXlatorBoolean}, {@link DPTXlator3BitControlled},
 * {@link DPTXlator8BitUnsigned}, {@link DPTXlator2ByteFloat}, {@link DPTXlatorString}.
 * Other translator types are loaded through {@link TranslatorTypes}.
 * 
 * @author B. Malinowsky
 */
public class ProcessCommunicatorImpl implements ProcessCommunicator
{
	private final class NLListener implements NetworkLinkListener
	{
		NLListener()
		{}

		public void indication(FrameEvent e)
		{
			final CEMILData f = (CEMILData) e.getFrame();
			final byte[] apdu = f.getPayload();
			final int svc = DataUnitBuilder.getAPDUService(apdu);
			// Note: even if this is a read response we have waited for,
			// we nevertheless notify the listeners about it (we do *not* discard it)
			if (svc == GROUP_RESPONSE && wait) {
				synchronized (indications) {
					indications.add(e);
					indications.notify();
				}
			}
			// notify listeners
			if (svc == GROUP_READ)
				fireGroupReadWrite(f, new byte[0], svc);
			else if (svc == GROUP_RESPONSE || svc == GROUP_WRITE)
				fireGroupReadWrite(f, DataUnitBuilder.extractASDU(apdu), svc);
			else
				logger.warn("unsupported APDU service - ignored, service code = 0x"
					+ Integer.toHexString(svc));
		}

		private void fireGroupReadWrite(CEMILData f, byte[] asdu, int svc)
		{
			final ProcessEvent e =
				new ProcessEvent(ProcessCommunicatorImpl.this, f.getSource(),
					(GroupAddress) f.getDestination(), asdu);
			for (final Iterator i = listeners.iterator(); i.hasNext();) {
				final Object l = i.next();
				try {
					if (svc == GROUP_READ && l instanceof ProcessListenerEx)
						((ProcessListenerEx) l).groupReadRequest(e);
					else if (svc == GROUP_RESPONSE
						&& l instanceof ProcessListenerEx)
						((ProcessListenerEx) l).groupReadResponse(e);
					else
						((ProcessListener) l).groupWrite(e);
				}
				catch (final RuntimeException rte) {
					removeProcessListener((ProcessListener) l);
					logger.error("removed event listener", rte);
				}
			}
		}

		public void confirmation(FrameEvent e)
		{}

		public void linkClosed(CloseEvent e)
		{
			logger.info("attached link was closed");
		}
	}

	private static final short GROUP_READ = 0x00;
	private static final short GROUP_RESPONSE = 0x40;
	private static final short GROUP_WRITE = 0x80;

	private final KNXNetworkLink lnk;
	private final NetworkLinkListener lnkListener = new NLListener();
	private final EventListeners listeners;
	private final List indications = new LinkedList();
	private Priority priority = Priority.LOW;
	// maximum wait time in seconds for a response message
	private int responseTimeout = 10;
	private volatile boolean wait;
	private volatile boolean detached;
	private final LogService logger;

	// translators are created local, since creation is not expensive
	// and we don't have to synchronize on them
//	private final DPTXlatorBoolean tbool;
//	private final DPTXlator3BitControlled t3bit;
//	private final DPTXlator2ByteFloat tfloat;
//	private final DPTXlatorString tstring;
//	private final DPTXlator8BitUnsigned tubyte;

	/**
	 * Creates a new process communicator attached to the supplied KNX network link.
	 * <p>
	 * The log service used by this process communicator is named "process " +
	 * <code>link.getName()</code>.
	 * 
	 * @param link network link used for communication with a KNX network
	 * @throws KNXLinkClosedException if the network link is closed
	 */
	public ProcessCommunicatorImpl(KNXNetworkLink link) throws KNXLinkClosedException
	{
		if (!link.isOpen())
			throw new KNXLinkClosedException();
		lnk = link;
		lnk.addLinkListener(lnkListener);
		logger = LogManager.getManager().getLogService("process " + link.getName());
		listeners = new EventListeners(logger);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#setResponseTimeout(int)
	 */
	public void setResponseTimeout(int timeout)
	{
		responseTimeout = timeout;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#getResponseTimeout()
	 */
	public int getResponseTimeout()
	{
		return responseTimeout;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#setPriority
	 * (tuwien.auto.calimero.Priority)
	 */
	public void setPriority(Priority p)
	{
		priority = p;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#getPriority()
	 */
	public Priority getPriority()
	{
		return priority;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#addProcessListener
	 * (tuwien.auto.calimero.process.ProcessListener)
	 */
	public void addProcessListener(ProcessListener l)
	{
		listeners.add(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#removeProcessListener
	 * (tuwien.auto.calimero.process.ProcessListener)
	 */
	public void removeProcessListener(ProcessListener l)
	{
		listeners.remove(l);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readBool
	 * (tuwien.auto.calimero.GroupAddress)
	 */
	public boolean readBool(GroupAddress dst) throws KNXTimeoutException,
		KNXRemoteException, KNXLinkClosedException, KNXFormatException
	{
		final byte[] apdu = readFromGroup(dst, priority, 0, 0);
		final DPTXlatorBoolean t = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_BOOL);
		extractGroupASDU(apdu, t);
		return t.getValueBoolean();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, boolean)
	 */
	public void write(GroupAddress dst, boolean value) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		try {
			final DPTXlatorBoolean t = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_BOOL);
			t.setValue(value);
			write(dst, priority, t);
		}
		catch (final KNXFormatException ignore) {}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readUnsigned
	 * (tuwien.auto.calimero.GroupAddress, java.lang.String)
	 */
	public short readUnsigned(GroupAddress dst, String scale)
		throws KNXTimeoutException, KNXRemoteException, KNXLinkClosedException,
		KNXFormatException
	{
		final byte[] apdu = readFromGroup(dst, priority, 1, 1);
		final DPTXlator8BitUnsigned t = new DPTXlator8BitUnsigned(scale);
		extractGroupASDU(apdu, t);
		return t.getValueUnsigned();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, int, java.lang.String)
	 */
	public void write(GroupAddress dst, int value, String scale)
		throws KNXTimeoutException, KNXFormatException, KNXLinkClosedException
	{
		final DPTXlator8BitUnsigned t = new DPTXlator8BitUnsigned(scale);
		t.setValue(value);
		write(dst, priority, t);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readControl
	 * (tuwien.auto.calimero.GroupAddress)
	 */
	public byte readControl(GroupAddress dst) throws KNXTimeoutException,
		KNXRemoteException, KNXLinkClosedException, KNXFormatException
	{
		final byte[] apdu = readFromGroup(dst, priority, 0, 0);
		final DPTXlator3BitControlled t =
			new DPTXlator3BitControlled(DPTXlator3BitControlled.DPT_CONTROL_DIMMING);
		extractGroupASDU(apdu, t);
		return t.getValueSigned();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, boolean, byte)
	 */
	public void write(GroupAddress dst, boolean control, byte stepcode)
		throws KNXTimeoutException, KNXFormatException, KNXLinkClosedException
	{
		final DPTXlator3BitControlled t =
			new DPTXlator3BitControlled(DPTXlator3BitControlled.DPT_CONTROL_DIMMING);
		t.setValue(control, stepcode);
		write(dst, priority, t);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readFloat
	 * (tuwien.auto.calimero.GroupAddress)
	 */
	public float readFloat(GroupAddress dst) throws KNXTimeoutException,
		KNXRemoteException, KNXLinkClosedException, KNXFormatException
	{
		final byte[] apdu = readFromGroup(dst, priority, 2, 2);
		final DPTXlator2ByteFloat t =
			new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE_DIFFERENCE);
		extractGroupASDU(apdu, t);
		return t.getValueFloat();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, float)
	 */
	public void write(GroupAddress dst, float value) throws KNXTimeoutException,
		KNXFormatException, KNXLinkClosedException
	{
		final DPTXlator2ByteFloat t =
			new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE_DIFFERENCE);
		t.setValue(value);
		write(dst, priority, t);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readUInt
	 * (tuwien.auto.calimero.GroupAddress)
	 */
	public long readUint(GroupAddress dst)
			throws KNXTimeoutException, KNXRemoteException, KNXLinkClosedException, KNXFormatException {
		final byte[] apdu = readFromGroup(dst, priority, 4, 4);
		final DPTXlator4ByteUnsigned t = new DPTXlator4ByteUnsigned(DPTXlator4ByteUnsigned.DPT_VALUE_4_UCOUNT);
		extractGroupASDU(apdu, t);
		return t.getValueUnsigned();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, long)
	 */
	public void write(GroupAddress dst, long value)
			throws KNXTimeoutException, KNXFormatException, KNXLinkClosedException {
		final DPTXlator4ByteUnsigned t = new DPTXlator4ByteUnsigned(DPTXlator4ByteUnsigned.DPT_VALUE_4_UCOUNT);
		t.setValue(value);
		write(dst, priority, t);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#readString
	 * (tuwien.auto.calimero.GroupAddress)
	 */
	public String readString(GroupAddress dst) throws KNXTimeoutException,
		KNXRemoteException, KNXLinkClosedException, KNXFormatException
	{
		final byte[] apdu = readFromGroup(dst, priority, 0, 14);
		final DPTXlatorString t = new DPTXlatorString(DPTXlatorString.DPT_STRING_8859_1);
		extractGroupASDU(apdu, t);
		return t.getValue();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.GroupAddress, java.lang.String)
	 */
	public void write(GroupAddress dst, String value) throws KNXTimeoutException,
		KNXFormatException, KNXLinkClosedException
	{
		final DPTXlatorString t = new DPTXlatorString(DPTXlatorString.DPT_STRING_8859_1);
		t.setValue(value);
		write(dst, priority, t);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#read
	 * (tuwien.auto.calimero.datapoint.Datapoint)
	 */
	public String read(Datapoint dp) throws KNXException
	{
		if (dp.getDPT() == null)
			throw new KNXIllegalArgumentException("specify DPT for datapoint");
		final byte[] apdu = readFromGroup(dp.getMainAddress(), dp.getPriority(), 0, 14);
		final DPTXlator t =
			TranslatorTypes.createTranslator(dp.getMainNumber(), dp.getDPT());
		extractGroupASDU(apdu, t);
		return t.getValue();
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#write
	 * (tuwien.auto.calimero.datapoint.Datapoint, java.lang.String)
	 */
	public void write(Datapoint dp, String value) throws KNXException
	{
		final DPTXlator t =
			TranslatorTypes.createTranslator(dp.getMainNumber(), dp.getDPT());
		t.setValue(value);
		write(dp.getMainAddress(), dp.getPriority(), t);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.process.ProcessCommunicator#detach()
	 */
	public KNXNetworkLink detach()
	{
		// if we synchronize on method we would take into account
		// a worst case blocking of response timeout seconds
		synchronized (lnkListener) {
			// wait of response time seconds
			if (detached)
				return null;
			detached = true;
		}
		lnk.removeLinkListener(lnkListener);
		fireDetached();
		logger.info("detached from " + lnk.getName());
		LogManager.getManager().removeLogService(logger.getName());
		return lnk;
	}

	private void write(GroupAddress dst, Priority p, DPTXlator t)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		if (detached)
			throw new KNXIllegalStateException("process communicator detached");
		lnk.sendRequestWait(dst, p, createGroupAPDU(GROUP_WRITE, t));
		logger.trace("group write to " + dst + " succeeded");
	}

	private synchronized byte[] readFromGroup(GroupAddress dst, Priority p,
		int minASDULen, int maxASDULen) throws KNXTimeoutException,
		KNXInvalidResponseException, KNXLinkClosedException
	{
		if (detached)
			throw new KNXIllegalStateException("process communicator detached");
		try {
			wait = true;
			lnk.sendRequestWait(dst, p, DataUnitBuilder.createCompactAPDU(
				GROUP_READ, null));
			logger.trace("sent group read request to " + dst);
			return waitForResponse(minASDULen + 2, maxASDULen + 2);
		}
		finally {
			wait = false;
		}
	}

	private byte[] waitForResponse(int minAPDU, int maxAPDU)
		throws KNXInvalidResponseException, KNXTimeoutException
	{
		long remaining = responseTimeout * 1000;
		final long end = System.currentTimeMillis() + remaining;
		synchronized (indications) {
			while (remaining > 0) {
				try {
					while (indications.size() > 0) {
						final FrameEvent e = (FrameEvent) indications.remove(0);
						final byte[] d = e.getFrame().getPayload();
						// ??? do check for empty list before
						indications.clear();
						// ok, got response we're waiting for
						if (d.length >= minAPDU && d.length <= maxAPDU)
							return d;
						else{
							// lets wait
							final String s = "APDU response length " + d.length +
									" bytes, expected "	+ minAPDU + " to " + maxAPDU;
							logger.warn("received group read response with " + s + " continuing waiting!");
						}
					
					}
					indications.wait(remaining);
				}
				catch (final InterruptedException e) {}
				remaining = end - System.currentTimeMillis();
			}
		}
		logger.info("timeout waiting for group read response");
		throw new KNXTimeoutException("timeout waiting for group read response");
	}

	private void fireDetached()
	{
		final DetachEvent e = new DetachEvent(this);
		for (final Iterator i = listeners.iterator(); i.hasNext();) {
			final ProcessListener l = (ProcessListener) i.next();
			try {
				l.detached(e);
			}
			catch (final RuntimeException rte) {
				removeProcessListener(l);
				logger.error("removed event listener", rte);
			}
		}
	}
	
	// createGroupAPDU and extractGroupASDU helper would actually better fit
	// into to DataUnitBuilder, but moved here to avoid DPT dependencies
	
	/**
	 * Creates a group service application layer protocol data unit containing all
	 * items of a DPT translator.
	 * <p>
	 * The transport layer bits in the first byte (TL / AL control field) are set 0. The
	 * maximum length used for the ASDU is not checked.<br>
	 * For DPTs occupying &lt;= 6 bits in length the optimized (compact) group write /
	 * response format layout is used.
	 *
	 * @param service application layer group service code
	 * @param t DPT translator with items to put into ASDU
	 * @return group APDU as byte array
	 */
	private static byte[] createGroupAPDU(int service, DPTXlator t)
	{
		// check for group read
		if (service == 0x00)
			return new byte[2];
		// only group response and group write are allowed
		if (service != 0x40 && service != 0x80)
			throw new KNXIllegalArgumentException("not an APDU group service");
		// determine if data starts at byte offset 1 (optimized) or 2 (default)
		final int offset = (t.getItems() == 1 && t.getTypeSize() == 0) ? 1 : 2;
		final byte[] buf = new byte[t.getItems() * Math.max(1, t.getTypeSize()) + offset];
		buf[0] = (byte) (service >> 8);
		buf[1] = (byte) service;
		return t.getData(buf, offset);
	}

	/**
	 * Extracts the service data unit of an application layer protocol data unit into a
	 * DPT translator.
	 * <p>
	 * The whole service data unit is taken as data for translation. If the length of the
	 * supplied <code>apdu</code> is 2, a compact group APDU format layout is assumed.<br>
	 * On return of this method, the supplied translator contains the DPT items from the
	 * ASDU.
	 *
	 * @param apdu application layer protocol data unit, 2 &lt;= apdu.length
	 * @param t the DPT translator to fill with the ASDU
	 */
	public static void extractGroupASDU(byte[] apdu, DPTXlator t)
	{
		if (apdu.length < 2)
			throw new KNXIllegalArgumentException("minimum APDU length is 2 bytes");
		t.setData(apdu, apdu.length == 2 ? 1 : 2);
	}
}
