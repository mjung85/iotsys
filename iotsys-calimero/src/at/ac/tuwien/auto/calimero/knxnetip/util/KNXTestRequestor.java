package at.ac.tuwien.auto.calimero.knxnetip.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.knxnetip.KNXnetIPTunnel;
import at.ac.tuwien.auto.calimero.link.KNXNetworkLinkIP;
import at.ac.tuwien.auto.calimero.link.event.NetworkLinkListener;
import at.ac.tuwien.auto.calimero.link.medium.TPSettings;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicatorImpl;


public class KNXTestRequestor {
	// Calimero NG
	private static KNXNetworkLinkIP nl;
	private static ProcessCommunicator pc;

	public static void main(String[] args){
		
		String routerHostname = "192.168.1.13";
		
		String detectedLocalIP = "";
		int curSimilarity = 0;
		

		try {
			InetAddress routerAddress = InetAddress
					.getByName(routerHostname);
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();

			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface ni = networkInterfaces.nextElement();
				Enumeration<InetAddress> inetAddresses = ni
						.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses
							.nextElement();
					String hostAddress = inetAddress.getHostAddress();
					int sim = similarity(
							routerAddress.getHostAddress(), hostAddress);
					if (sim >= curSimilarity) {
						curSimilarity = sim;
						detectedLocalIP = hostAddress;
					}

				}
			}
		
		String localIP = detectedLocalIP;
		try {
			nl = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL,
					new InetSocketAddress(InetAddress.getByName(localIP), 0),
					new InetSocketAddress(
							InetAddress.getByName(routerHostname), 3671),
					false, new TPSettings(false));
			System.out.println("My individiual KNX address is: "
					+ nl.getKNXMedium().getDeviceAddress());
			pc = new ProcessCommunicatorImpl(nl);
			
			for(int i= 0 ; i< 250; i++){
				GroupAddress comfortMode = new GroupAddress("0/1/3");
				pc.write(comfortMode, true);
				Thread.sleep(300);
				KNXnetIPTunnel.numRequest++;
				System.out.println("Val is now: " + pc.readBool(comfortMode) + " request: " + KNXnetIPTunnel.numRequest );
				KNXnetIPTunnel.numRequest++;
				Thread.sleep(300);
				
				pc.write(comfortMode, false);
				KNXnetIPTunnel.numRequest++;
				Thread.sleep(300);
				System.out.println("Val is now: " + pc.readBool(comfortMode) + " request: " + KNXnetIPTunnel.numRequest );
				KNXnetIPTunnel.numRequest++;
				Thread.sleep(300);
			}
			
//			pc.write(dp, value)	
			nl.close();
			System.exit(0);
			//nl.addLinkListener(new NetworkLinkListener());
		} catch (UnknownHostException | KNXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static int similarity(String s1, String s2) {
		int i = 0;
		for (i = 0; i < Math.min(s1.length(), s2.length()); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				break;
			}
		}
		return i;
	}
}	
