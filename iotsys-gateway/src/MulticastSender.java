import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MulticastSender {
	public static void main(String[] args){
		String msg = "Hello";
		InetAddress group;
		try {
			group = InetAddress.getByName("FF02:FFFF::1");

			MulticastSocket s = new MulticastSocket();
		
			System.out.println("Hit enter for sending messages. Hit q for quiting.");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
			while(!reader.readLine().equals("q")){
				DatagramPacket hi = new DatagramPacket(msg.getBytes(),
						msg.length(), group, 5683);
				System.out.println("Sending msg.");
				s.send(hi);
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
