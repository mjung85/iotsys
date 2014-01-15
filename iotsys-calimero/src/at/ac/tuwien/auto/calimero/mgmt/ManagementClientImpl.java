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

package at.ac.tuwien.auto.calimero.mgmt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.DataUnitBuilder;
import at.ac.tuwien.auto.calimero.DetachEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXInvalidResponseException;
import at.ac.tuwien.auto.calimero.exception.KNXRemoteException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.link.KNXLinkClosedException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLink;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.log.LogService;


/**
 * Implementation of management client.
 * <p>
 * Uses {@link TransportLayer} internally for communication. <br>
 * All management service methods invoked after a detach of the network link are allowed
 * to throw {@link KNXIllegalStateException}.
 * 
 * @author B. Malinowsky
 */
public class ManagementClientImpl implements ManagementClient
{
	private static final short ADC_READ = 0x0180;
	private static final short ADC_RESPONSE = 0x01C0;

	private static final short AUTHORIZE_READ = 0x03D1;
	private static final short AUTHORIZE_RESPONSE = 0x03D2;

	private static final short DOA_WRITE = 0x3E0;
	private static final short DOA_READ = 0x3E1;
	private static final short DOA_RESPONSE = 0x3E2;
	private static final short DOA_SELECTIVE_READ = 0x3E3;

	private static final short IND_ADDR_READ = 0x0100;
	private static final short IND_ADDR_RESPONSE = 0x0140;
	private static final short IND_ADDR_WRITE = 0xC0;

	private static final short IND_ADDR_SN_READ = 0x03DC;
	private static final short IND_ADDR_SN_RESPONSE = 0x03DD;
	private static final short IND_ADDR_SN_WRITE = 0x03DE;

	private static final short DEVICE_DESC_READ = 0x300;
	private static final short DEVICE_DESC_RESPONSE = 0x340;

	private static final short KEY_WRITE = 0x03D3;
	private static final short KEY_RESPONSE = 0x03D4;

	private static final short MEMORY_READ = 0x0200;
	private static final short MEMORY_RESPONSE = 0x0240;
	private static final short MEMORY_WRITE = 0x0280;

	private static final short PROPERTY_DESC_READ = 0x03D8;
	private static final short PROPERTY_DESC_RESPONSE = 0x03D9;

	private static final short PROPERTY_READ = 0x03D5;
	private static final short PROPERTY_RESPONSE = 0x03D6;
	private static final short PROPERTY_WRITE = 0x03D7;

	private static final short RESTART = 0x0380;

	private final class TLListener implements TransportListener
	{
		TLListener()
		{}

		public void broadcast(FrameEvent e)
		{
			checkResponse(e);
		}

		public void dataConnected(FrameEvent e)
		{
			checkResponse(e);
		}

		public void dataIndividual(FrameEvent e)
		{
			checkResponse(e);
		}

		public void disconnected(Destination d)
		{}

		public void group(FrameEvent e)
		{}

		public void detached(DetachEvent e)
		{}

		public void linkClosed(CloseEvent e)
		{
			logger.info("attached link was closed");
		}

		private void checkResponse(FrameEvent e)
		{
			if (svcResponse != 0) {
				final byte[] tpdu = e.getFrame().getPayload();
				if (DataUnitBuilder.getAPDUService(tpdu) == svcResponse)
					synchronized (indications) {
						indications.add(e);
						indications.notify();
					}
			}
		}
	};

	private final TransportLayer tl;
	private final TLListener tlListener = new TLListener();
	private volatile Priority priority = Priority.LOW;
	private volatile int responseTimeout = 5;
	private final List indications = new LinkedList();
	private volatile int svcResponse;
	private volatile boolean detached;
	private final LogService logger;

	/**
	 * Creates a new management client attached to the supplied KNX network link.
	 * <p>
	 * The log service used by this management client is named "MC " +
	 * <code>link.getName()</code>.
	 * 
	 * @param link network link used for communication with a KNX network
	 * @throws KNXLinkClosedException if the network link is closed
	 */
	public ManagementClientImpl(KNXNetworkLink link) throws KNXLinkClosedException
	{
		tl = new TransportLayerImpl(link);
		tl.addTransportListener(tlListener);
		logger = LogManager.getManager().getLogService("MC " + link.getName());
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#setResponseTimeout(int)
	 */
	public void setResponseTimeout(int timeout)
	{
		if (timeout <= 0)
			throw new KNXIllegalArgumentException("timeout not > 0");
		responseTimeout = timeout;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#getResponseTimeout()
	 */
	public int getResponseTimeout()
	{
		return responseTimeout;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#setPriority
	 * (tuwien.auto.calimero.Priority)
	 */
	public void setPriority(Priority p)
	{
		priority = p;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#getPriority()
	 */
	public Priority getPriority()
	{
		return priority;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#createDestination
	 * (tuwien.auto.calimero.IndividualAddress, boolean)
	 */
	public Destination createDestination(IndividualAddress remote,
		boolean connectionOriented)
	{
		return tl.createDestination(remote, connectionOriented);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#createDestination
	 * (tuwien.auto.calimero.IndividualAddress, boolean, boolean, boolean)
	 */
	public Destination createDestination(IndividualAddress remote,
		boolean connectionOriented, boolean keepAlive, boolean verifyMode)
	{
		return tl.createDestination(remote, connectionOriented, keepAlive, verifyMode);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeAddress
	 * (tuwien.auto.calimero.IndividualAddress)
	 */
	public void writeAddress(IndividualAddress newAddress) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		tl.broadcast(false, Priority.SYSTEM, DataUnitBuilder.createAPDU(IND_ADDR_WRITE,
			newAddress.toByteArray()));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readAddress(boolean)
	 */
	public synchronized IndividualAddress[] readAddress(boolean oneAddressOnly)
		throws KNXTimeoutException, KNXRemoteException, KNXLinkClosedException
	{
		final List l = new ArrayList();
		try {
			svcResponse = IND_ADDR_RESPONSE;
			tl.broadcast(false, Priority.SYSTEM, DataUnitBuilder.createCompactAPDU(
				IND_ADDR_READ, null));
			long wait = responseTimeout * 1000;
			final long end = System.currentTimeMillis() + wait;
			while (wait > 0) {
				l.add(new IndividualAddress(waitForResponse(0, 0, wait)));
				if (oneAddressOnly)
					break;
				wait = end - System.currentTimeMillis();
			}
		}
		catch (final KNXTimeoutException e) {
			if (l.isEmpty())
				throw e;
		}
		finally {
			svcResponse = 0;
		}
		return (IndividualAddress[]) l.toArray(new IndividualAddress[l.size()]);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeAddress
	 * (byte[], tuwien.auto.calimero.IndividualAddress)
	 */
	public void writeAddress(byte[] serialNo, IndividualAddress newAddress)
		throws KNXTimeoutException, KNXLinkClosedException
	{
		if (serialNo.length != 6)
			throw new KNXIllegalArgumentException("length of serial number not 6 bytes");
		final byte[] asdu = new byte[12];
		for (int i = 0; i < 6; ++i)
			asdu[i] = serialNo[i];
		asdu[6] = (byte) (newAddress.getRawAddress() >>> 8);
		asdu[7] = (byte) newAddress.getRawAddress();
		tl.broadcast(false, Priority.SYSTEM, DataUnitBuilder.createAPDU(
			IND_ADDR_SN_WRITE, asdu));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readAddress(byte[])
	 */
	public synchronized IndividualAddress readAddress(byte[] serialNo)
		throws KNXTimeoutException, KNXRemoteException, KNXLinkClosedException
	{
		if (serialNo.length != 6)
			throw new KNXIllegalArgumentException("length of serial number not 6 bytes");
		try {
			svcResponse = IND_ADDR_SN_RESPONSE;
			tl.broadcast(false, Priority.SYSTEM, DataUnitBuilder.createAPDU(
				IND_ADDR_SN_READ, serialNo));
			return new IndividualAddress(waitForResponse(10, 10));
		}
		finally {
			svcResponse = 0;
		}
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeDomainAddress(byte[])
	 */
	public void writeDomainAddress(byte[] domain) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		if (domain.length != 2 && domain.length != 6)
			throw new KNXIllegalArgumentException("invalid length of domain address");
		tl.broadcast(true, priority, DataUnitBuilder.createAPDU(DOA_WRITE, domain));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readDomainAddress(boolean)
	 */
	public synchronized List readDomainAddress(boolean oneDomainOnly)
		throws KNXLinkClosedException, KNXInvalidResponseException, KNXTimeoutException
	{
		// we allow 6 bytes ASDU for RF domains
		return makeDOAs(readBroadcast(priority, DataUnitBuilder.createCompactAPDU(
			DOA_READ, null), DOA_RESPONSE, 6, 6, oneDomainOnly));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readDomainAddress
	 * (byte[], tuwien.auto.calimero.IndividualAddress, int)
	 */
	public List readDomainAddress(byte[] domain, IndividualAddress start, int range)
		throws KNXInvalidResponseException, KNXLinkClosedException, KNXTimeoutException
	{
		if (domain.length != 2)
			throw new KNXIllegalArgumentException("length of domain address not 2 bytes");
		if (range < 0 || range > 255)
			throw new KNXIllegalArgumentException("range out of range [0..255]");
		final byte[] addr = start.toByteArray();
		return makeDOAs(readBroadcast(priority, DataUnitBuilder.createAPDU(
			DOA_SELECTIVE_READ, new byte[] { domain[0], domain[1], addr[0], addr[1],
				(byte) range }), DOA_RESPONSE, 2, 2, false));
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readDeviceDesc
	 * (tuwien.auto.calimero.mgmt.Destination, int)
	 */
	public byte[] readDeviceDesc(Destination dst, int descType)
		throws KNXInvalidResponseException, KNXDisconnectException, KNXTimeoutException,
		KNXLinkClosedException
	{
		if (descType < 0 || descType > 63)
			throw new KNXIllegalArgumentException("descriptor type out of range [0..63]");
		final byte[] apdu =
			sendWait2(dst, priority, DataUnitBuilder.createCompactAPDU(DEVICE_DESC_READ,
				new byte[] { (byte) descType }), DEVICE_DESC_RESPONSE, 2, 14);
		final byte[] dd = new byte[apdu.length - 2];
		for (int i = 0; i < apdu.length - 2; ++i)
			dd[i] = apdu[2 + i];
		return dd;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#restart
	 * (tuwien.auto.calimero.mgmt.Destination)
	 */
	public void restart(Destination dst) throws KNXTimeoutException,
		KNXLinkClosedException
	{
		final byte[] send = DataUnitBuilder.createCompactAPDU(RESTART, null);
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing restart in connectionless mode, " + dst.toString());
		tl.sendData(dst.getAddress(), priority, send);
		// force a reconnect at later time
		tl.disconnect(dst);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readProperty
	 * (tuwien.auto.calimero.mgmt.Destination, int, int, int, int)
	 */
	public byte[] readProperty(Destination dst, int objIndex, int propID, int start,
		int elements) throws KNXTimeoutException, KNXRemoteException,
		KNXDisconnectException, KNXLinkClosedException
	{
		if (objIndex < 0 || objIndex > 255 || propID < 0 || propID > 255 || start < 0
			|| start > 0xFFF || elements < 0 || elements > 15)
			throw new KNXIllegalArgumentException("argument value out of range");
		final byte[] asdu = new byte[4];
		asdu[0] = (byte) objIndex;
		asdu[1] = (byte) propID;
		asdu[2] = (byte) ((elements << 4) | ((start >>> 8) & 0xF));
		asdu[3] = (byte) start;

		final byte[] apdu = sendWait2(dst, priority, DataUnitBuilder.createAPDU(
			PROPERTY_READ, asdu), PROPERTY_RESPONSE, 4, 14);
		// check if number of elements is 0, indicates access problem
		final int number = (apdu[4] & 0xFF) >>> 4;
		if (number == 0)
			throw new KNXRemoteException("property access failed/forbidden");
		if (number != elements)
			throw new KNXInvalidResponseException("number of elements differ");
		final byte[] prop = new byte[apdu.length - 6];
		for (int i = 0; i < prop.length; ++i)
			prop[i] = apdu[i + 6];
		return prop;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeProperty
	 * (tuwien.auto.calimero.mgmt.Destination, int, int, int, int, byte[])
	 */
	public void writeProperty(Destination dst, int objIndex, int propID, int start,
		int elements, byte[] data) throws KNXTimeoutException, KNXRemoteException,
		KNXDisconnectException, KNXLinkClosedException
	{
		if (objIndex < 0 || objIndex > 255 || propID < 0 || propID > 255 || start < 0
			|| start > 0xFFF || data.length == 0 || elements < 0 || elements > 15)
			throw new KNXIllegalArgumentException("argument value out of range");
		final byte[] asdu = new byte[4 + data.length];
		asdu[0] = (byte) objIndex;
		asdu[1] = (byte) propID;
		asdu[2] = (byte) ((elements << 4) | ((start >>> 8) & 0xF));
		asdu[3] = (byte) start;
		for (int i = 0; i < data.length; ++i)
			asdu[4 + i] = data[i];
		final byte[] send = DataUnitBuilder.createAPDU(PROPERTY_WRITE, asdu);
		final byte[] apdu = sendWait2(dst, priority, send, PROPERTY_RESPONSE, 4, 14);
		// if number of elements is 0, remote app had problems
		final int elems = (apdu[4] & 0xFF) >> 4;
		if (elems == 0)
			throw new KNXRemoteException("property write failed/forbidden");
		if (elems != elements)
			throw new KNXInvalidResponseException("number of elements differ");
		if (data.length != apdu.length - 6)
			throw new KNXInvalidResponseException("data lengths differ, bytes: "
				+ data.length + " written, " + (apdu.length - 6) + " response");
		// explicitly read back written properties
		for (int i = 4; i < asdu.length; ++i)
			if (apdu[2 + i] != asdu[i])
				throw new KNXRemoteException("read back failed (erroneous property data)");
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readPropertyDesc
	 * (tuwien.auto.calimero.mgmt.Destination, int, int, int)
	 */
	public byte[] readPropertyDesc(Destination dst, int objIndex, int propID,
		int propIndex) throws KNXTimeoutException, KNXRemoteException,
		KNXDisconnectException, KNXLinkClosedException
	{
		if (objIndex < 0 || objIndex > 255 || propID < 0 || propID > 255 || propIndex < 0
			|| propIndex > 255)
			throw new KNXIllegalArgumentException("argument value out of range");
		final byte[] send =	DataUnitBuilder.createAPDU(PROPERTY_DESC_READ, new byte[] {
			(byte) objIndex, (byte) propID, (byte) (propID == 0 ? propIndex : 0) });
		final byte[] apdu = sendWait2(dst, priority, send, PROPERTY_DESC_RESPONSE, 7, 7);
		// max_nr_elem field is a 4bit exponent + 12bit unsigned
		// on problem this field is 0
		if (apdu[6] == 0 && apdu[7] == 0)
			throw new KNXRemoteException(
				"got no property description (object non-existant?)");
		return new byte[] { apdu[2], apdu[3], apdu[4], apdu[5], apdu[6], apdu[7], apdu[8] };
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readADC
	 * (tuwien.auto.calimero.mgmt.Destination, int, int)
	 */
	public int readADC(Destination dst, int channelNr, int repeat)
		throws KNXTimeoutException, KNXDisconnectException, KNXRemoteException,
		KNXLinkClosedException
	{
		if (channelNr < 0 || channelNr > 63 || repeat < 0 || repeat > 255)
			throw new KNXIllegalArgumentException("ADC arguments out of range");
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing read ADC in connectionless mode, " + dst.toString());
		final byte[] apdu =
			sendWait(dst, priority, DataUnitBuilder.createCompactAPDU(ADC_READ,
				new byte[] { (byte) channelNr, (byte) repeat }), ADC_RESPONSE, 3, 3);
		if (apdu[2] == 0)
			throw new KNXRemoteException("error reading value of A/D converter");
		return ((apdu[3] & 0xff) << 8) | apdu[4] & 0xff;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#readMemory
	 * (tuwien.auto.calimero.mgmt.Destination, int, int)
	 */
	public byte[] readMemory(Destination dst, int startAddr, int bytes)
		throws KNXTimeoutException, KNXDisconnectException, KNXRemoteException,
		KNXLinkClosedException
	{
		if (startAddr < 0 || startAddr > 0xFFFF || bytes < 1 || bytes > 63)
			throw new KNXIllegalArgumentException("argument value out of range");
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing read memory in connectionless mode, " + dst.toString());
		final byte[] apdu =	sendWait(dst, priority, DataUnitBuilder.createCompactAPDU(
			MEMORY_READ, new byte[] { (byte) bytes, (byte) (startAddr >>> 8),
				(byte) startAddr }), MEMORY_RESPONSE, 2, 65);
		int no = apdu[1] & 0x3F;
		if (no == 0)
			throw new KNXRemoteException("could not read memory from 0x"
				+ Integer.toHexString(startAddr));
		final byte[] mem = new byte[no];
		while (--no >= 0)
			mem[no] = apdu[4 + no];
		return mem;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeMemory
	 * (tuwien.auto.calimero.mgmt.Destination, int, byte[])
	 */
	public void writeMemory(Destination dst, int startAddr, byte[] data)
		throws KNXDisconnectException, KNXTimeoutException, KNXRemoteException,
		KNXLinkClosedException
	{
		if (startAddr < 0 || startAddr > 0xFFFF || data.length == 0 || data.length > 63)
			throw new KNXIllegalArgumentException("argument value out of range");
		final byte[] asdu = new byte[data.length + 3];
		asdu[0] = (byte) data.length;
		asdu[1] = (byte) (startAddr >> 8);
		asdu[2] = (byte) startAddr;
		for (int i = 0; i < data.length; ++i)
			asdu[3 + i] = data[i];
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing write memory in connectionless mode, " + dst.toString());
		final byte[] send = DataUnitBuilder.createCompactAPDU(MEMORY_WRITE, asdu);
		if (dst.isVerifyMode()) {
			// explicitly read back data
			final byte[] apdu = sendWait(dst, priority, send, MEMORY_RESPONSE, 2, 65);
			if ((apdu[1] & 0x3f) == 0)
				throw new KNXRemoteException("remote app. could not write memory");
			if (apdu.length - 4 != data.length)
				throw new KNXInvalidResponseException("number of memory bytes differ");
			for (int i = 4; i < apdu.length; ++i)
				if (apdu[i] != asdu[i - 1])
					throw new KNXRemoteException("verify failed (erroneous memory data)");
		}
		else
			tl.sendData(dst, priority, send);
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#authorize
	 * (tuwien.auto.calimero.mgmt.Destination, byte[])
	 */
	public byte authorize(Destination dst, byte[] key) throws KNXDisconnectException,
		KNXTimeoutException, KNXRemoteException, KNXLinkClosedException
	{
		if (key.length != 4)
			throw new KNXIllegalArgumentException("length of authorize key not 4 bytes");
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing authorize in connectionless mode, " + dst.toString());
		final byte[] apdu =
			sendWait(dst, priority, DataUnitBuilder.createAPDU(AUTHORIZE_READ, key),
				AUTHORIZE_RESPONSE, 1, 1);
		final int level = apdu[2] & 0xff;
		if (level > 15)
			throw new KNXInvalidResponseException(
				"authorization level out of range [0..15]");
		return (byte) level;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#writeKey
	 * (tuwien.auto.calimero.mgmt.Destination, int, byte[])
	 */
	public void writeKey(Destination dst, int level, byte[] key)
		throws KNXTimeoutException, KNXDisconnectException, KNXRemoteException,
		KNXLinkClosedException
	{
		// level 255 is free access
		if (level < 0 || level > 254 || key.length != 4)
			throw new KNXIllegalArgumentException(
				"level out of range or key length not 4 bytes");
		if (dst.isConnectionOriented())
			tl.connect(dst);
		else
			logger.error("doing write key in connectionless mode, " + dst.toString());
		final byte[] apdu =
			sendWait(dst, priority, DataUnitBuilder.createAPDU(KEY_WRITE, new byte[] {
				(byte) level, key[0], key[1], key[2], key[3] }), KEY_RESPONSE, 1, 1);
		if ((apdu[1] & 0xFF) == 0xFF)
			throw new KNXRemoteException(
				"access denied: current access level > write level");
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#isOpen()
	 */
	public boolean isOpen()
	{
		return !detached;
	}

	/* (non-Javadoc)
	 * @see tuwien.auto.calimero.mgmt.ManagementClient#detach()
	 */
	public KNXNetworkLink detach()
	{
		final KNXNetworkLink lnk = tl.detach();
		if (lnk != null) {
			logger.info("detached from " + lnk.getName());
			LogManager.getManager().removeLogService(logger.getName());
		}
		detached = true;
		return lnk;
	}

	private synchronized byte[] sendWait(Destination d, Priority p, byte[] apdu,
		int response, int minASDULen, int maxASDULen) throws KNXDisconnectException,
		KNXTimeoutException, KNXInvalidResponseException, KNXLinkClosedException
	{
		try {
			svcResponse = response;
			tl.sendData(d, p, apdu);
			return waitForResponse(minASDULen, maxASDULen);
		}
		finally {
			svcResponse = 0;
		}
	}

	private synchronized byte[] sendWait2(Destination d, Priority p, byte[] apdu,
		int response, int minASDULen, int maxASDULen) throws KNXDisconnectException,
		KNXTimeoutException, KNXInvalidResponseException, KNXLinkClosedException
	{
		try {
			svcResponse = response;
			if (d.isConnectionOriented()) {
				tl.connect(d);
				tl.sendData(d, p, apdu);
			}
			else
				tl.sendData(d.getAddress(), p, apdu);
			return waitForResponse(minASDULen, maxASDULen);
		}
		finally {
			svcResponse = 0;
		}
	}

	// timeout in milliseconds
	// min + max ASDU len are *not* including any field that contains ACPI
	private byte[] waitForResponse(int minASDULen, int maxASDULen, long timeout)
		throws KNXInvalidResponseException, KNXTimeoutException
	{
		long remaining = timeout;
		final long end = System.currentTimeMillis() + remaining;
		synchronized (indications) {
			while (remaining > 0) {
				try {
					while (indications.size() > 0) {
						final CEMI frame =
							((FrameEvent) indications.remove(0)).getFrame();
						final byte[] apdu = frame.getPayload();
						assert svcResponse == DataUnitBuilder.getAPDUService(apdu);
						if (apdu.length < minASDULen + 2 || apdu.length > maxASDULen + 2) {
							final String s = "invalid ASDU response length "
								+ (apdu.length - 2) + " bytes, expected " + minASDULen
								+ " to " + maxASDULen;
							logger.error("received response with " + s);
							throw new KNXInvalidResponseException(s);
						}
						if (svcResponse == IND_ADDR_RESPONSE
							|| svcResponse == IND_ADDR_SN_RESPONSE)
							return ((CEMILData) frame).getSource().toByteArray();
						indications.clear();
						return apdu;
					}
					indications.wait(remaining);
				}
				catch (final InterruptedException e) {}
				remaining = end - System.currentTimeMillis();
			}
		}
		throw new KNXTimeoutException("timeout occurred while waiting for data response");
	}

	private byte[] waitForResponse(int minASDULen, int maxASDULen)
		throws KNXInvalidResponseException, KNXTimeoutException
	{
		return waitForResponse(minASDULen, maxASDULen, responseTimeout * 1000);
	}

	private synchronized List readBroadcast(Priority p, byte[] apdu, int response,
		int minASDULen, int maxASDULen, boolean oneOnly) throws KNXLinkClosedException,
		KNXInvalidResponseException, KNXTimeoutException
	{
		final List l = new ArrayList();
		try {
			svcResponse = response;
			tl.broadcast(true, p, apdu);
			long wait = responseTimeout * 1000;
			final long end = System.currentTimeMillis() + wait;
			while (wait > 0) {
				l.add(waitForResponse(minASDULen, maxASDULen, wait));
				if (oneOnly)
					break;
				wait = end - System.currentTimeMillis();
			}
		}
		catch (final KNXTimeoutException e) {
			if (l.isEmpty())
				throw e;
		}
		finally {
			svcResponse = 0;
		}
		return l;
	}

	// cut domain addresses out of APDUs
	private List makeDOAs(List l)
	{
		for (int i = 0; i < l.size(); ++i) {
			final byte[] pdu = (byte[]) l.get(i);
			if (pdu.length == 4)
				l.set(i, new byte[] { pdu[2], pdu[3] });
			else if (pdu.length == 8)
				l.set(i, new byte[] { pdu[2], pdu[3], pdu[4], pdu[5], pdu[6], pdu[7] });
		}
		return l;
	}
}
