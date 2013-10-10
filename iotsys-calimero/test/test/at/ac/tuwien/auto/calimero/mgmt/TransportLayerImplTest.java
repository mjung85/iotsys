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

package test.at.ac.tuwien.auto.calimero.mgmt;

import java.util.List;
import java.util.Vector;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.DetachEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.link.KNXLinkClosedException;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLink;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLinkIP;
import at.ac.tuwien.auto.calimero.link.medium.TPSettings;
import at.ac.tuwien.auto.calimero.log.LogManager;
import at.ac.tuwien.auto.calimero.mgmt.Destination;
import at.ac.tuwien.auto.calimero.mgmt.KNXDisconnectException;
import at.ac.tuwien.auto.calimero.mgmt.TransportLayer;
import at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl;
import at.ac.tuwien.auto.calimero.mgmt.TransportListener;

import junit.framework.TestCase;
import test.at.ac.tuwien.auto.calimero.Util;
import test.at.ac.tuwien.auto.calimero.knxnetip.Debug;

/**
 * @author B. Malinowsky
 */
public class TransportLayerImplTest extends TestCase
{
	private KNXNetworkLink nl;
	private TransportLayer tl;
	private TLListener ltl;
	private Destination dco;
	private Destination dcl;
	private final Priority p = Priority.NORMAL;

	// device descriptor read
	private final int devDescRead = 0x300;
	private final byte[] tsduDescRead =
		new byte[] { (byte) (devDescRead >> 8), (byte) (devDescRead | 0) };

	private final class TLListener implements TransportListener
	{
		List broad = new Vector();
		List conn = new Vector();
		List ind = new Vector();
		List group = new Vector();
		List dis = new Vector();
		volatile boolean closed;
		volatile boolean detached;

		TLListener()
		{}

		public void broadcast(FrameEvent e)
		{
			assertNotNull(e);
			broad.add(e.getFrame());
			final CEMILData f = (CEMILData) e.getFrame();
			assertEquals(new GroupAddress(0), f.getDestination());
			System.out.println("broadcast:");
			Debug.printLData(f);
		}

		public void dataConnected(FrameEvent e)
		{
			assertNotNull(e);
			conn.add(e.getFrame());
			final CEMILData f = (CEMILData) e.getFrame();
			System.out.println("connecteddata:");
			Debug.printLData(f);
		}

		public void dataIndividual(FrameEvent e)
		{
			assertNotNull(e);
			ind.add(e.getFrame());
			final CEMILData f = (CEMILData) e.getFrame();
			assertTrue(f.getDestination() instanceof IndividualAddress);
			System.out.println("individual:");
			Debug.printLData(f);
		}

		public void disconnected(Destination d)
		{
			assertNotNull(d);
			dis.add(d);
			System.out.println("disconnected: " + d);
		}

		public void group(FrameEvent e)
		{
			assertNotNull(e);
			group.add(e.getFrame());
			final CEMILData f = (CEMILData) e.getFrame();
			assertTrue(f.getDestination() instanceof GroupAddress);
			Debug.printLData(f);
		}

		public void linkClosed(CloseEvent e)
		{
			assertNotNull(e);
			assertEquals(nl, e.getSource());
			if (closed)
				fail("already closed");
			closed = true;
		}

		public void detached(DetachEvent e)
		{
			detached = true;
		}
	};

	/**
	 * @param name name of test case
	 */
	public TransportLayerImplTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		LogManager.getManager().addWriter(null, Util.getLogWriter());
		nl = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, Util.getLocalHost(), Util
			.getServer(), false, TPSettings.TP1);
		tl = new TransportLayerImpl(nl);
		ltl = new TLListener();
		tl.addTransportListener(ltl);
		dco = tl.createDestination(Util.getRouterAddress(), true);
		dcl = tl.createDestination(new IndividualAddress(3, 1, 1), false);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		if (tl != null) {
			tl.detach();
		}
		nl.close();
		dco.destroy();
		dcl.destroy();

		LogManager.getManager().removeWriter(null, Util.getLogWriter());
		super.tearDown();
	}

	public final void testTransportLayerImpl()
	{
		nl.close();
		try {
			new TransportLayerImpl(nl);
			fail("link closed");
		}
		catch (final KNXLinkClosedException e) {}
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#addTransportListener
	 * (at.ac.tuwien.auto.calimero.mgmt.TransportListener)}.
	 */
	public final void testAddEventListener()
	{
		tl.addTransportListener(new TLListener());
		tl.addTransportListener(ltl);
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#broadcast
	 * (boolean, Priority, byte[])}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 * @throws InterruptedException
	 */
	public final void testBroadcast() throws KNXTimeoutException, KNXLinkClosedException,
		InterruptedException
	{
		final int indAddrRead = 0x0100;
		final byte[] tsdu = new byte[] { (byte) (indAddrRead >> 8), (byte) indAddrRead };
		System.out.println("\nensure at least one programming button is pressed");
		Thread.sleep(5000);
		tl.broadcast(false, Priority.SYSTEM, tsdu);
		Thread.sleep(100);
		assertFalse(ltl.broad.isEmpty());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#connect
	 * (at.ac.tuwien.auto.calimero.mgmt.Destination)}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testConnect() throws KNXTimeoutException, KNXLinkClosedException
	{
		tl.connect(dco);
		tl.connect(dco);

		tl.connect(dcl);

		final TransportLayer tl2 = new TransportLayerImpl(nl);
		final Destination d2 =
			tl2.createDestination(new IndividualAddress(3, 1, 1), true);
		try {
			tl.connect(d2);
			fail("not owning destination");
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#createDestination
	 * (at.ac.tuwien.auto.calimero.IndividualAddress, boolean)}.
	 */
	public final void testCreateDestinationIndividualAddressBoolean()
	{
		assertTrue(dco.isConnectionOriented());
		assertEquals(Util.getRouterAddress(), dco.getAddress());
		dco.destroy();
		final Destination d = tl.createDestination(Util.getRouterAddress(), true);
		assertNotSame(dco, d);
		assertEquals(Util.getRouterAddress(), d.getAddress());
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#createDestination
	 * (at.ac.tuwien.auto.calimero.IndividualAddress, boolean, boolean, boolean)}.
	 */
	public final void testCreateDestinationIndividualAddressBooleanBooleanBoolean()
	{
		Destination d;
		try {
			d = tl.createDestination(Util.getRouterAddress(), true, false, true);
			fail("already created");
		}
		catch (final KNXIllegalArgumentException e) {}

		d = tl.createDestination(new IndividualAddress(3, 2, 1), true, false, true);
		assertTrue(d.isConnectionOriented());
		assertEquals(new IndividualAddress(3, 2, 1), d.getAddress());
		assertFalse(d.isKeepAlive());
		assertTrue(d.isVerifyMode());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#detach()}.
	 * 
	 * @throws KNXLinkClosedException
	 */
	public final void testDetach() throws KNXLinkClosedException
	{
		assertFalse(ltl.detached);
		tl.detach();
		assertTrue(ltl.detached);
		ltl.detached = false;
		tl.detach();
		assertFalse(ltl.detached);

		final TransportLayer tl2 = new TransportLayerImpl(nl);
		final TLListener l2 = new TLListener();
		tl2.addTransportListener(l2);
		assertFalse(l2.detached);
		tl2.detach();
		assertTrue(l2.detached);
		l2.detached = false;
		tl2.detach();
		assertFalse(l2.detached);
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#detach()}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testDetach2() throws KNXTimeoutException, KNXLinkClosedException
	{
		tl.detach();
		tl.detach();
		try {
			tl.sendData(new IndividualAddress(4, 4, 4), p, new byte[] { 0, 0 });
			fail("we are detached");
		}
		catch (final KNXIllegalStateException e) {}
		try {
			tl.connect(dco);
			fail("we are detached");
		}
		catch (final KNXIllegalStateException e) {}
		try {
			tl.createDestination(new IndividualAddress(5, 5, 5), false, false, false);
			fail("we are detached");
		}
		catch (final KNXIllegalStateException e) {}
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#detach()}.
	 */
	public final void testDetach3()
	{
		nl.close();
		tl.detach();
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#disconnect
	 * (at.ac.tuwien.auto.calimero.mgmt.Destination)}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 */
	public final void testDisconnect() throws KNXLinkClosedException, KNXTimeoutException
	{
		tl.disconnect(dco);
		tl.disconnect(dco);
		tl.disconnect(dcl);
		assertFalse(ltl.dis.contains(dcl));

		assertFalse(ltl.dis.contains(dco));
		tl.connect(dco);
		assertFalse(ltl.dis.contains(dco));
		tl.disconnect(dco);
		assertTrue(ltl.dis.contains(dco));
		ltl.dis.clear();
		tl.disconnect(dco);
		assertFalse(ltl.dis.contains(dco));
		tl.disconnect(dcl);

		final TransportLayer tl2 = new TransportLayerImpl(nl);
		final Destination d2 =
			tl2.createDestination(new IndividualAddress(3, 1, 1), true);
		tl2.connect(d2);
		try {
			tl.disconnect(d2);
			fail("not the owner of destination");
		}
		catch (final KNXIllegalArgumentException e) {}
		d2.destroy();
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#getName()}.
	 */
	public final void testGetName()
	{
		String n = tl.getName();
		assertTrue(n.indexOf(nl.getName()) > -1);
		assertTrue(n.indexOf("TL") > -1);
		tl.detach();
		n = tl.getName();
		assertNotNull(n);
		assertTrue(n.indexOf("TL") > -1);
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#removeTransportListener
	 * (at.ac.tuwien.auto.calimero.mgmt.TransportListener)}.
	 */
	public final void testRemoveEventListener()
	{
		tl.removeTransportListener(ltl);
		tl.removeTransportListener(ltl);
		// should do nothing
		tl.removeTransportListener(new TLListener());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#sendData
	 * (at.ac.tuwien.auto.calimero.mgmt.Destination, at.ac.tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 * @throws KNXDisconnectException
	 * @throws InterruptedException
	 */
	public final void testSendDataDestinationPriorityByteArray()
		throws KNXDisconnectException, KNXTimeoutException, KNXLinkClosedException,
		InterruptedException
	{
		try {
			tl.sendData(dco, p, tsduDescRead);
			fail("disconnected");
		}
		catch (final KNXDisconnectException e) {}

		tl.connect(dco);
		tl.sendData(dco, p, tsduDescRead);
		Thread.sleep(100);
		assertTrue(ltl.conn.size() == 1);
		// second time
		tl.sendData(dco, p, tsduDescRead);
		Thread.sleep(100);
		assertTrue(ltl.conn.size() == 2);
		tl.disconnect(dco);

		try {
			tl.sendData(dco, p, tsduDescRead);
			fail("disconnected");
		}
		catch (final KNXDisconnectException e) {}
		assertTrue(ltl.conn.size() == 2);

		final TransportLayer tl2 = new TransportLayerImpl(nl);
		final Destination d2 =
			tl2.createDestination(new IndividualAddress(3, 1, 1), true);
		try {
			tl.sendData(d2, p, tsduDescRead);
			fail("not owning destination");
		}
		catch (final KNXIllegalArgumentException e) {}
	}

	public void testSendDataNonExistingAddress() throws KNXTimeoutException,
		KNXLinkClosedException, InterruptedException
	{
		// not existing device address
		final Destination dunknown =
			tl.createDestination(new IndividualAddress(5, 5, 5), true);
		tl.connect(dunknown);
		try {
			tl.sendData(dunknown, p, tsduDescRead);
			tl.disconnect(dunknown);
			fail("destination should been disconnected");
		}
		catch (final KNXDisconnectException e) {}
		Thread.sleep(100);
		assertTrue(ltl.conn.size() == 0);
	}

	public void testSendDataDetach() throws KNXTimeoutException, KNXLinkClosedException,
		InterruptedException
	{
		// do a detach while waiting for L4 ack
		tl.connect(dco);
		try {
			final Thread detacher = new Thread()
			{
				public void run()
				{
					synchronized (this) {
						notify();
					}
					try {
						sleep(10);
					}
					catch (final InterruptedException e) {}
					tl.detach();
				}
			};
			synchronized (detacher) {
				detacher.start();
				detacher.wait();
			}
			tl.sendData(dco, p, tsduDescRead);
			fail("we got detached");
		}
		catch (final KNXDisconnectException e) {}
		// for TL listener to process remaining indications
		Thread.sleep(500);
	}

	public void testSendDataLinkClose() throws KNXTimeoutException,
		KNXLinkClosedException, KNXDisconnectException, InterruptedException
	{
		// do a link closed while waiting for L4 response
		tl.connect(dco);
		tl.sendData(dco, p, tsduDescRead);
		Thread.sleep(10);
		nl.close();
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.mgmt.TransportLayerImpl#sendData
	 * (at.ac.tuwien.auto.calimero2.KNXAddress, at.ac.tuwien.auto.calimero.Priority, byte[])}.
	 * 
	 * @throws KNXLinkClosedException
	 * @throws KNXTimeoutException
	 * @throws InterruptedException
	 */
	public final void testSendDataKNXAddressPriorityByteArray()
		throws KNXTimeoutException, KNXLinkClosedException, InterruptedException
	{
		final int propRead = 0x03D5;
		// read pid max_apdu_length
		final byte[] tsdu =
			new byte[] { (byte) (propRead >> 8), (byte) propRead, 0, 56, 0x10, 1, };
		tl.sendData(Util.getRouterAddress(), p, tsdu);
		Thread.sleep(1500);
		assertFalse(ltl.ind.isEmpty());
	}
}
