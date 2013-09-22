package javax.jmdns.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
            JmDNS jmdns = JmDNS.create(InetAddress.getByName("143.248.56.162"));
            System.out.println("Opened JmDNS!");

            ServiceInfo subTypedService = ServiceInfo.create("_obix._coap.local.", "aFanSpeed", "_sunblindactuator", 8080, null);
            ServiceInfo subTypedService2 = ServiceInfo.create("_obix._coap.local.", "aFanSpeed2", "_fanspeedactuator", 8081, null);
            ServiceInfo aService = ServiceInfo.create("_obix._udp.local.", "aSimpleService", 8082, null);
            try {
            	subTypedService.setIpv6Addr("2001:629:2500:570::11d");
            	aService.setIpv6Addr("2001:629:2500:570::11f");
            	aService.setServer("a.b.c.");
            	subTypedService2.setIpv6Addr("2001:629:2500:570::11a");
    		} catch (Exception e1){
    			e1.printStackTrace();
    		}
            //jmdns.registerService(subTypedService);
            //jmdns.registerService(subTypedService2);
            jmdns.registerService(aService);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
