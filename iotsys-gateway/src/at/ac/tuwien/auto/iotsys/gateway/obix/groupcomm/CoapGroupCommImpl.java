package at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.registries.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.registries.OptionNumberRegistry;

import obix.Obj;
import obix.Str;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.IoTSySDevice;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommService;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;


public class CoapGroupCommImpl extends GroupCommImpl{
	private static final Logger log = Logger.getLogger(CoapGroupCommImpl.class.getName());

	public CoapGroupCommImpl(Obj datapoint, GroupCommService groupCommService) {
		super(datapoint, groupCommService);
	}
	
	public synchronized Obj joinGroup(Obj in){
		log.info("CoAP join group.");
		
		if(datapoint.getParent() instanceof IoTSySDevice){
			final String busUri = ((IoTSySDevice) datapoint.getParent()).getBusAddress() + "/" + datapoint.getHref() + "/groupComm/joinGroup";
			
			Str str = (Str) in;
			try {
				Inet6Address inet6 = (Inet6Address) Inet6Address.getByName(str.get());
				
				String formatted = String.format("%04X:%04X:%04X:%04X:%04X:%04X:%04X:%04X", (((short) inet6.getAddress()[0]) *256 + inet6.getAddress()[1]) & 0XFFFF, (((short) inet6.getAddress()[2]) *256 + inet6.getAddress()[3]) & 0XFFFF, (((short) inet6.getAddress()[4]) *256 + inet6.getAddress()[5]) & 0XFFFF,
																							(((short) inet6.getAddress()[6]) *256 + inet6.getAddress()[7]) & 0XFFFF, (((short) inet6.getAddress()[8]) *256 + inet6.getAddress()[9]) & 0XFFFF, (((short) inet6.getAddress()[10]) *256 + inet6.getAddress()[11]) & 0XFFFF,
																							(((short) inet6.getAddress()[12]) *256 + inet6.getAddress()[13]) & 0XFFFF, (((short) inet6.getAddress()[14]) *256 + inet6.getAddress()[15]) & 0XFFFF  );
				
				
				GroupCommServiceImpl.getInstance().registerAsReceiver(inet6, this.datapoint);
				
				String payload = "<str val=\"" + formatted + "\">";
				
				Request request = new POSTRequest();
				System.out.println("Adresse: " + busUri + "\nPayload: " + payload);
				request.setPayload(payload);
				request.setType(messageType.CON);
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
				request.setOption(new Option(16, OptionNumberRegistry.SIZE));
			
				// specify URI of target endpoint
				request.setURI(busUri);
				// enable response queue for blocking I/O
				request.enableResponseQueue(true);						
				
				try {
					request.execute();
					
				} catch (IOException e) {
					System.err.println("Failed to execute request: " + e.getMessage());
				}
				
				// receive response
				try {
					Response response = request.receiveResponse();
					if(response != null && response.getPayloadString() != null){
						return new Obj();
					}				
				} catch (InterruptedException e) {
					System.err.println("Receiving of response interrupted: "
							+ e.getMessage());
				}	
				
				return new Obj();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
		return new Obj();
	}
	
	public synchronized Obj leaveGroup(Obj in){
		log.info("CoAP leave group.");
		
		if(datapoint.getParent() instanceof IoTSySDevice){
			final String tempUri = ((IoTSySDevice) datapoint.getParent()).getBusAddress() + "/" + datapoint.getHref() + "/groupComm/leaveGroup";
			
			Str str = (Str) in;
			try {
				Inet6Address inet6 = (Inet6Address) Inet6Address.getByName(str.get());
				
				String formatted = String.format("%04X:%04X:%04X:%04X:%04X:%04X:%04X:%04X", (((short) inet6.getAddress()[0]) *256 + inet6.getAddress()[1]) & 0XFFFF, (((short) inet6.getAddress()[2]) *256 + inet6.getAddress()[3]) & 0XFFFF, (((short) inet6.getAddress()[4]) *256 + inet6.getAddress()[5]) & 0XFFFF,
																							(((short) inet6.getAddress()[6]) *256 + inet6.getAddress()[7]) & 0XFFFF, (((short) inet6.getAddress()[8]) *256 + inet6.getAddress()[9]) & 0XFFFF, (((short) inet6.getAddress()[10]) *256 + inet6.getAddress()[11]) & 0XFFFF,
																							(((short) inet6.getAddress()[12]) *256 + inet6.getAddress()[13]) & 0XFFFF, (((short) inet6.getAddress()[14]) *256 + inet6.getAddress()[15]) & 0XFFFF  );
				String payload = "<str val=\"" + formatted + "\">";
				GroupCommServiceImpl.getInstance().unregisterReceiverObject(inet6, this.datapoint);
				Request request = new POSTRequest();
				System.out.println("Adresse: " + tempUri + "\nPayload: " + payload);
			
				request.setType(messageType.CON);
				request.setOption(new Option(MediaTypeRegistry.APPLICATION_XML,OptionNumberRegistry.ACCEPT));
			
				// specify URI of target endpoint
				request.setURI(tempUri);
				request.setPayload(payload);
				// enable response queue for blocking I/O
				request.enableResponseQueue(true);
				
				try {
					request.execute();
					
				} catch (IOException e) {
					System.err.println("Failed to execute request: " + e.getMessage());
				}
				
				// receive response
				try {
					Response response = request.receiveResponse();
					if(response != null && response.getPayloadString() != null){
						return new Obj();
					}				
				} catch (InterruptedException e) {
					System.err.println("Receiving of response interrupted: "
							+ e.getMessage());
				}	
				
				return new Obj();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}	
		}
		return new Obj();
	}

}
