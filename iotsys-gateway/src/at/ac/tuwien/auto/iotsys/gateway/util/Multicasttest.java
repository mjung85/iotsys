package at.ac.tuwien.auto.iotsys.gateway.util;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;

public class Multicasttest {

	public static void main(String[] args) {
		try {
			String msg = "Hello";
			InetAddress group = InetAddress.getByName("FF02:FFFF::1");

			MulticastSocket s = new MulticastSocket(null);
		
			s.setReuseAddress(true);
			s.bind(new InetSocketAddress(9999));
			s.joinGroup(group);

			DatagramPacket hi = new DatagramPacket(msg.getBytes(),
					msg.length(), group, 5683);
			System.out.println("Sending msg.");
			s.send(hi);
			// get their responses!
			byte[] buf = new byte[1000];

			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			while (System.in.read() >= 0) {
				s.receive(recv);
				String rcv = new String(buf);
				System.out.println("Received: " + rcv);
			}
			
			// OK, I'm done talking - leave the group...
			s.leaveGroup(group);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
