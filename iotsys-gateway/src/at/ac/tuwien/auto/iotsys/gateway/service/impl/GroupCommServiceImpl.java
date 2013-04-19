package at.ac.tuwien.auto.iotsys.gateway.service.impl;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Communicator;
import ch.ethz.inf.vs.californium.coap.Message.messageType;
import ch.ethz.inf.vs.californium.coap.PUTRequest;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.io.ObixEncoder;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommService;

/**
 * This class takes care for the group communication.
 */
public class GroupCommServiceImpl implements GroupCommService {
	private static final Logger log = Logger.getLogger(GroupCommServiceImpl.class.getName());

	private final static GroupCommServiceImpl instance = new GroupCommServiceImpl();

	private static final Hashtable<Inet6Address, Hashtable<String, Obj>> groupObjectPerAddress = new Hashtable<Inet6Address, Hashtable<String, Obj>>();

	private GroupCommServiceImpl() {

	}

	public static GroupCommService getInstance() {
		return instance;
	}

	@Override
	public void handleRequest(Inet6Address group, Obj payload) {
	
		log.finest("Handle request for " + group + ", " + payload);
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				for (Obj obj : groupObjects.values()) {
					obj.writeObject(payload);
				}
			} 
			else{
				log.info("No group objects found!");
			}
		}
	}

	@Override
	public void registerObject(Inet6Address group, Obj obj)  {
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);

			if (groupObjects != null) {
				groupObjects.put(obj.getFullContextPath(), obj);
			}
			else{
				// we need to create a multicast socket for the specified port
				try {
					Communicator.getInstance().getUDPLayer().openMulticastSocket(group);
					Hashtable<String, Obj> newGroupObjects = new Hashtable<String, Obj>();
					groupObjectPerAddress.put(group, newGroupObjects);
					newGroupObjects.put(obj.getFullContextPath(), obj);
				} catch (SocketException e) {			
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void unregisterObject(Inet6Address group, Obj obj) {
		synchronized (groupObjectPerAddress) {
			Hashtable<String, Obj> groupObjects = groupObjectPerAddress
					.get(group);
			if (groupObjects != null) {
				groupObjects.remove(obj.getFullContextPath());
				
				if(groupObjects.size() == 0){
					// close group comm socket
					try {
						Communicator.getInstance().getUDPLayer().closeMulticastSocket(group);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					groupObjectPerAddress.remove(group);				
				}
			}
		}
	}

	@Override
	public void sendUpdate(Inet6Address group, Object state) {
		log.finest("Sending new state of object " + state + " to group " + group.getHostAddress());
		PUTRequest putRequest = new PUTRequest();
		putRequest.setType(messageType.NON);
		putRequest
				.setURI("coap://[" + group.getHostAddress() + "]/");
		
		if(state instanceof Bool){
			Bool bool = (Bool) state;
			Bool b = new Bool();
			b.set(bool.get());
			String payload = ObixEncoder.toString(b);
			putRequest.setPayload(payload);
		}
		else if(state instanceof Real){
			Real real = (Real) state;
			Real r = new Real();
			r.set(real.get());
			String payload = ObixEncoder.toString(r);
			putRequest.setPayload(payload);
		} else if(state instanceof Int){
			Int intObj = (Int) state;
			Int i = new Int();
			i.set(intObj.get());
			String payload = ObixEncoder.toString(i);
			putRequest.setPayload(payload);
		} 
	
		putRequest.enableResponseQueue(true);
		try {
			putRequest.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

