package javax.jmdns.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/**
 * @author Nam Giang - zang at kaist dot ac dot kr
 *
 */
public class TestRegisterService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
            System.out.println("Opening JmDNS...");
            JmDNS jmdns = JmDNS.create(InetAddress.getByName("fe80::acbc:b659:71db:5cb7%20"));
            System.out.println("Opened JmDNS!");

            ServiceInfo subTypedService = ServiceInfo.create("_obix._coap.local.", "aFanSpeed", "_fanspeedactuator", 8080, null);
            try {
            	subTypedService.setIpv6Addr("2001:629:2500:570::11d");
    		} catch (IllegalStateException | UnknownHostException | SecurityException e1) {
    			e1.printStackTrace();
    		}
            jmdns.registerService(subTypedService);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
