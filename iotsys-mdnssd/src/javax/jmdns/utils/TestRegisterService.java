package javax.jmdns.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;

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
            JmDNS jmdns = JmDNS.create(InetAddress.getByName(PropertiesLoader.getInstance().getProperties()
					.getProperty("iotsys.gateway.authNsAddr", "fe80::acbc:b659:71db:5cb7%20")));
            System.out.println("Opened JmDNS!");

            ServiceInfo subTypedService = ServiceInfo.create("_obix._coap.local.", "aFanSpeed", "_sunblindactuator", 8080, null);
            ServiceInfo subTypedService2 = ServiceInfo.create("_obix._coap.local.", "aFanSpeed2", "_fanspeedactuator", 8081, null);
            ServiceInfo aService = ServiceInfo.create("_obix._udp.local.", "aSimpleService", 8082, null);
            
            final HashMap<String, String> values = new HashMap<String, String>();
            values.put("A text", "text");
            
            try {
            	subTypedService.setIpv6Addr("2001:629:2500:570::11d");
            	aService.setIpv6Addr("2001:629:2500:570::11f");aService.setText(values);
            	aService.setServer("asdf.virtualdevice.iotsys.com");
            	subTypedService2.setIpv6Addr("2001:629:2500:570::11a");
    		} catch (Exception e1){
    			e1.printStackTrace();
    		}
            jmdns.registerService(subTypedService);
            jmdns.registerService(subTypedService2);
            jmdns.registerService(aService);

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
