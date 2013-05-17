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

package test.at.ac.tuwien.auto.calimero.knxnetip;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Vector;

import at.ac.tuwien.auto.calimero.CloseEvent;
import at.ac.tuwien.auto.calimero.FrameEvent;
import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.IndividualAddress;
import at.ac.tuwien.auto.calimero.KNXListener;
import at.ac.tuwien.auto.calimero.Priority;
import at.ac.tuwien.auto.calimero.cemi.CEMI;
import at.ac.tuwien.auto.calimero.cemi.CEMIBusMon;
import at.ac.tuwien.auto.calimero.cemi.CEMILData;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalArgumentException;
import at.ac.tuwien.auto.calimero.exception.KNXIllegalStateException;
import at.ac.tuwien.auto.calimero.exception.KNXTimeoutException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXConnectionClosedException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import at.ac.tuwien.auto.calimero.log.LogManager;

import junit.framework.TestCase;
import test.at.ac.tuwien.auto.calimero.Util;

/**
 * @author B. Malinowsky
 */
public class KNXnetIPTunnelTest extends TestCase
{
	private static KNXnetIPConnection.BlockingMode noblock =
		KNXnetIPConnection.NONBLOCKING;
	private static KNXnetIPConnection.BlockingMode ack = KNXnetIPConnection.WAIT_FOR_ACK;
	private static KNXnetIPConnection.BlockingMode con = KNXnetIPConnection.WAIT_FOR_CON;

	private KNXnetIPTunnel t;
	private KNXnetIPTunnel tnat;
	private KNXnetIPTunnel mon;

	private KNXListenerImpl l;
	private KNXListenerImpl lnat;
	private KNXListenerImpl lmon;
	private CEMILData frame;
	private CEMILData frame2;
	// should be a frame with unused destination address
	private CEMILData frameNoDest;

	private final class KNXListenerImpl implements KNXListener
	{
		boolean closed;
		CEMI received;
		List fifoReceived = new Vector();

		public void frameReceived(FrameEvent e)
		{
			assertNotNull(e);
			if (this == l)
				assertEquals(t, e.getSource());
			if (this == lnat)
				assertEquals(tnat, e.getSource());
			if (this == lmon)
				assertEquals(mon, e.getSource());
			received = e.getFrame();
			if (e.getFrame() instanceof CEMIBusMon) {
				Debug.printMonData((CEMIBusMon) e.getFrame());
			}
			// Debug.parseLData((CEMILData) received);
			fifoReceived.add(e.getFrame());
		}

		public void connectionClosed(CloseEvent e)
		{
			assertNotNull(e);
			if (this == l)
				assertEquals(t, e.getSource());
			if (this == lnat)
				assertEquals(tnat, e.getSource());
			if (this == lmon)
				assertEquals(mon, e.getSource());
			if (closed)
				fail("already closed");
			closed = true;
		}
	}

	/**
	 * @param name name of test case
	 */
	public KNXnetIPTunnelTest(String name)
	{
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		l = new KNXListenerImpl();
		lnat = new KNXListenerImpl();
		lmon = new KNXListenerImpl();

		LogManager.getManager().addWriter(null, Util.getLogWriter());

		frame =
			new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
				new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 1) },
				Priority.NORMAL);
		frame2 =
			new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
				new GroupAddress(0, 0, 1), new byte[] { 0, (byte) (0x80 | 0) },
				Priority.URGENT);
		frameNoDest =
			new CEMILData(CEMILData.MC_LDATA_REQ, new IndividualAddress(0),
				new GroupAddress(10, 7, 10), new byte[] { 0, (byte) (0x80 | 0) },
				Priority.LOW);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		if (t != null) {
			t.close();
		}
		if (tnat != null) {
			tnat.close();
		}
		if (mon != null)
			mon.close();
		LogManager.getManager().removeWriter(null, Util.getLogWriter());
		super.tearDown();
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#send
	 * (at.ac.tuwien.auto.calimero.cemi.CEMI,
	 * at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection.BlockingMode)}.
	 * 
	 * @throws KNXException
	 */
	public final void testSend() throws KNXException
	{
		newTunnel();
		doSend(frame, con, true);
		doSend(frame2, con, true);
		doSend(frameNoDest, noblock, false);
		doSend(frame, ack, true);
		doSend(frame2, con, true);

		final long start = System.currentTimeMillis();
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		doSend(frameNoDest, con, false);
		final long end = System.currentTimeMillis();
		System.out.println("time for 10 send with con: " + (end - start));
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#send
	 * (at.ac.tuwien.auto.calimero.cemi.CEMI,
	 * at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection.BlockingMode)}.
	 * 
	 * @throws KNXException
	 * @throws InterruptedException
	 */
	public final void testFIFOSend() throws KNXException,
		InterruptedException
	{
		final int sends = 10;
		final List frames = new Vector();
		for (int i = 0; i < sends; i++) {
			frames.add(new CEMILData(CEMILData.MC_LDATA_REQ,
				new IndividualAddress(i + 1), new GroupAddress(2, 2, 2), new byte[] { 0,
					(byte) (0x80 | (i % 2)) }, Priority.LOW));
		}
		class Sender extends Thread
		{
			Sender(String name)
			{
				super(name);
			}

			public void run()
			{
				try {
					final CEMILData f = (CEMILData) frames.remove(0);
					synchronized (this) {
						notify();
					}
					t.send(f, con);
					System.out.println(getName() + " returned sending " + f.getSource());
				}
				catch (final KNXTimeoutException e) {
					e.printStackTrace();
				}
				catch (final KNXConnectionClosedException e) {
					e.printStackTrace();
				}
			}
		}

		newTunnel();
		final Thread[] threads = new Thread[sends];
		for (int i = 0; i < sends; i++) {
			threads[i] = new Sender("sender " + (i + 1));
		}
		Thread.sleep(50);
		final long start = System.currentTimeMillis();
		for (int i = 0; i < threads.length; i++) {
			synchronized (threads[i]) {
				threads[i].start();
				threads[i].wait();
			}
			Thread.sleep(20);
		}
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		final long end = System.currentTimeMillis();
		System.out.println("time for " + sends + " send MT with con: " + (end - start));
		assertEquals(sends, l.fifoReceived.size());
		for (int i = 0; i < sends; i++) {
			assertEquals(new IndividualAddress(i + 1),
				((CEMILData) l.fifoReceived.get(i)).getSource());
		}
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#send
	 * (at.ac.tuwien.auto.calimero.cemi.CEMI,
	 * at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection.BlockingMode)}.
	 * 
	 * @throws KNXException
	 */
	public final void testNATSend() throws KNXException
	{
		if (!Util.TEST_NAT) {
			System.out.println("\n==== skip testNATSend ====\n");
			return;
		}
		newNATTunnel();
		doNATSend(frame, con, true);
		doNATSend(frame2, con, true);
		doNATSend(frameNoDest, con, true);
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#send
	 * (at.ac.tuwien.auto.calimero.cemi.CEMI,
	 * at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPConnection.BlockingMode)}.
	 * 
	 * @throws KNXException
	 */
	public final void testMonitorSend() throws KNXException
	{
		newMonitor();
		try {
			mon.send(frame, ack);
			fail("no send in busmon");
		}
		catch (final KNXIllegalStateException e) {}
		// on open monitor test behavior on new tunnel
		try {
			newTunnel();
			fail("no tunnel on busmonitor");
		}
		catch (final KNXException e) {}
	}

	public final void testTunnelWithMonitor() throws KNXException
	{
		newTunnel();
		try {
			newMonitor();
			fail("no monitor on open tunnel");
		}
		catch (final KNXException e) {}
	}

	public final void testReceive() throws KNXException
	{
		newTunnel();
		System.out.println("Tunnel: waiting for some incoming frames...");
		try {
			Thread.sleep(30 * 1000);
		}
		catch (final InterruptedException e) {}
	}

	public final void testReceiveMonitor() throws KNXException
	{
		newMonitor();
		System.out.println("Monitor: waiting for some incoming frames...");
		try {
			Thread.sleep(30 * 1000);
		}
		catch (final InterruptedException e) {}
	}

	private void doSend(CEMILData f, KNXnetIPConnection.BlockingMode m,
		boolean positiveConfirmation) throws KNXTimeoutException,
		KNXConnectionClosedException
	{
		l.received = null;
		t.send(f, m);
		if (m == noblock) {
			while (t.getState() == KNXnetIPTunnel.ACK_PENDING)
				try {
					Thread.sleep(10);
				}
				catch (final InterruptedException e) {}
		}
		if (m == ack || m == noblock) {
			while (t.getState() == KNXnetIPTunnel.CEMI_CON_PENDING)
				try {
					Thread.sleep(10);
				}
				catch (final InterruptedException e) {}

		}
		assertNotNull(l.received);
		final CEMILData fcon = (CEMILData) l.received;
		assertEquals(positiveConfirmation, fcon.isPositiveConfirmation());
		l.received = null;
	}

	private void doNATSend(CEMILData f, KNXnetIPConnection.BlockingMode m,
		boolean positiveConfirmation) throws KNXTimeoutException,
		KNXConnectionClosedException
	{
		lnat.received = null;
		tnat.send(f, m);
		assertNotNull(lnat.received);
		final CEMILData fcon = (CEMILData) lnat.received;
		assertEquals(positiveConfirmation, fcon.isPositiveConfirmation());
		lnat.received = null;
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#KNXnetIPTunnel
	 * (short, java.net.InetSocketAddress, java.net.InetSocketAddress, boolean)}.
	 * 
	 * @throws KNXException
	 */
	public final void testKNXnetIPTunnel() throws KNXException
	{
		try {
			new KNXnetIPTunnel(KNXnetIPTunnel.LINK_LAYER, null, new InetSocketAddress(
				"127.0.0.1", 4000), false);
			fail("local socket is null");
		}
		catch (final KNXIllegalArgumentException e) {}

		try {
			new KNXnetIPTunnel(KNXnetIPTunnel.LINK_LAYER, new InetSocketAddress(
				"0.0.0.0", 0), new InetSocketAddress("127.0.0.1", 4000), false);
			fail("wildcard for local socket not null");
		}
		catch (final KNXIllegalArgumentException e) {}

		newTunnel();
		assertEquals(KNXnetIPConnection.OK, t.getState());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#KNXnetIPTunnel
	 * (short, java.net.InetSocketAddress, java.net.InetSocketAddress, boolean)}.
	 * 
	 * @throws KNXException
	 */
	public final void testKNXnetIPMonitor() throws KNXException
	{
		newMonitor();
		assertEquals(KNXnetIPConnection.OK, mon.getState());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#close()}.
	 * 
	 * @throws KNXException
	 */
	public final void testClose() throws KNXException
	{
		newTunnel();
		t.close();
		assertEquals(KNXnetIPConnection.CLOSED, t.getState());
		try {
			t.send(frame, con);
			fail("we are closed");
		}
		catch (final KNXConnectionClosedException e) {}
		assertEquals(KNXnetIPConnection.CLOSED, t.getState());
	}

	/**
	 * Test method for
	 * {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#getRemoteAddress()}.
	 * 
	 * @throws KNXException
	 */
	public final void testGetRemoteAddress() throws KNXException
	{
		newTunnel();
		assertEquals(Util.getServer(), t.getRemoteAddress());
		t.close();
		assertTrue(t.getRemoteAddress().getAddress().isAnyLocalAddress());
		assertTrue(t.getRemoteAddress().getPort() == 0);
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#getState()}.
	 * 
	 * @throws KNXException
	 * @throws InterruptedException
	 */
	public final void testGetState() throws KNXException,
		InterruptedException
	{
		newTunnel();
		assertEquals(KNXnetIPConnection.OK, t.getState());
		System.out.println("Testing heartbeat, will take some minutes");
		// give some seconds space for delay so we're on the save side
		Thread.sleep(4000);
		Thread.sleep(60000);
		assertEquals(KNXnetIPConnection.OK, t.getState());
		Thread.sleep(60000);
		assertEquals(KNXnetIPConnection.OK, t.getState());
	}

	/**
	 * Test method for {@link at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel#getState()}.
	 * 
	 * @throws KNXException
	 * @throws InterruptedException
	 */
	public final void testMonitorGetState() throws KNXException,
		InterruptedException
	{
		newMonitor();
		assertEquals(KNXnetIPConnection.OK, mon.getState());
		System.out.println("Testing heartbeat, will take some minutes");
		// give some seconds space for delay so we're on the save side
		Thread.sleep(4000);
		Thread.sleep(60000);
		assertEquals(KNXnetIPConnection.OK, mon.getState());
		Thread.sleep(60000);
		assertEquals(KNXnetIPConnection.OK, mon.getState());
	}

	private void newTunnel() throws KNXException
	{
		t =
			new KNXnetIPTunnel(KNXnetIPTunnel.LINK_LAYER, Util.getLocalHost(), Util
				.getServer(), false);
		t.addConnectionListener(l);
	}

	private void newNATTunnel() throws KNXException
	{
		tnat =
			new KNXnetIPTunnel(KNXnetIPTunnel.LINK_LAYER, Util.getLocalHost(), Util
				.getServer(), true);
		tnat.addConnectionListener(lnat);
	}

	private void newMonitor() throws KNXException
	{
		mon =
			new KNXnetIPTunnel(KNXnetIPTunnel.BUSMONITOR_LAYER, Util.getLocalHost(), Util
				.getServer(), false);
		mon.addConnectionListener(lmon);
	}
}
