/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

package at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker;

import java.io.*;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.OperationHandler;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.*;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.LobbyImpl;

import obix.*;
import obix.io.*;

public class ObjectBrokerImpl implements ObjectBroker{

	private static final Logger log = Logger.getLogger(ObjectBrokerImpl.class.getName());
	
	private HashMap<String, Obj> _objects;

	private LobbyImpl iotLobby = null;

	private WatchServiceImpl watchServiceImpl = null;
	
	private AboutImpl aboutImpl = null;

	private final Hashtable<String, OperationHandler> operationHandler = new Hashtable<String, OperationHandler>();

	private HashMap<String, String> _ipv6Mapping = new HashMap<String, String>();

	private final ArrayList<Obj> orderedObjects = new ArrayList<Obj>();
	
	private static final ObjectBroker instance = new ObjectBrokerImpl();
	
	private ObjectRefresher objectRefresher = new ObjectRefresher();

	/**
	 * Constructor for non-existing mapping logic
	 * 
	 */
	private ObjectBrokerImpl() {
		// _ml = new MappingLogic();
		_objects = new HashMap<String, Obj>();
		iotLobby = new LobbyImpl();
		
		
		aboutImpl = new AboutImpl();
		
		watchServiceImpl = new WatchServiceImpl(this);
		initInternals();
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#get_ipv6MappingTable()
	 */
	@Override
	public HashMap<String, String> get_ipv6MappingTable() {
		return _ipv6Mapping;
	}

	private void initInternals() {
		addObj(watchServiceImpl);
		addObj(aboutImpl,false); // About is added directly in lobby as local reference
		
		Thread t = new Thread(objectRefresher);
		t.start();
	}


	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#pullObj(obix.Uri)
	 */
	@Override
	public Obj pullObj(Uri href) {

		// if the path pointing to the lobby has been entered, the lobby is
		// returned
		String path = href.getPath();

		// oBIX lobby
		if (path.equals("/obix") || path.equals("/obix/"))
			return iotLobby;

		// else, the href references an internal object -> look it up in the
		// object database

		Obj o = _objects.get(href.getPath());
		if (o != null) {
			o.refreshObject();
		}

		// if the object could not be found, return an error
		if (o == null)
			return new Err("Object not found");

		return o;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#pushObj(obix.Uri, obix.Obj, boolean)
	 */
	@Override
	public Obj pushObj(Uri href, Obj input, boolean isOp) throws Exception {

		Obj o = _objects.get(href.getPath());

		if (o == null)
			throw new Exception("Object with URI " + href.get() + " not found");
		// write to the object should be handled by the according class
		if (!isOp) {
			input.setInvokedHref(href.get());
			log.finer("Writing on " + o);
			o.writeObject(input);

			return o;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#loadObjects(java.io.File)
	 */
	@Override
	public void loadObjects(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));

		String objStream = new String();

		String nextline = in.readLine();
		while (!(nextline == null)) {
			objStream += nextline;
			if ((nextline.endsWith("</obj>"))
					|| ((nextline.contains("obj")) && (nextline.contains("/>")))) {
				addObj(ObixDecoder.fromString(objStream.toString()));
				objStream = new String();
			}
		}
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#saveObjects(java.io.File)
	 */
	@Override
	public void saveObjects(File file) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#addObj(obix.Obj, java.lang.String)
	 */
	@Override
	public ArrayList<String> addObj(Obj o, String ipv6Address) {

		try {
			// generate Inet6Address in format "/:::::::"
			Inet6Address generateIPv6Address = (Inet6Address) Inet6Address
					.getByName(ipv6Address);
		

			if(o.getParent() == null && !o.getHref().isAbsolute()){ // root obj need to have an absolute URL
				o.setHref(new Uri("http://localhost" + (o.getHref().toString().startsWith("/")?"":"/") + o.getHref().toString()));
			}
			String href = o.getFullContextPath();

			_ipv6Mapping.put(generateIPv6Address.toString(), href);
	

			// add kids
			if (o.size() > 0) {
				Obj[] kids = o.list();
				for (int i = 0; i < o.size(); i++) {
					if (kids[i].getHref() != null) {
						_ipv6Mapping.put(generateIPv6Address.toString() + "/"
								+ kids[i].getHref(), href);
	
					}
				}
			}

			return addObj(o);

		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#getIPv6LinkedHref(java.lang.String)
	 */
	@Override
	public String getIPv6LinkedHref(String ipv6Address) {
		return _ipv6Mapping.get(ipv6Address);
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#containsIPv6(java.lang.String)
	 */
	@Override
	public boolean containsIPv6(String ipv6Address) {
		return _ipv6Mapping.containsKey(ipv6Address);
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#addObj(obix.Obj)
	 */
	@Override
	public ArrayList<String> addObj(Obj o) {
		
		
		return addObj(o, true);
	}
	
	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#addObj(obix.Obj, boolean)
	 */
	@Override
	public ArrayList<String> addObj(Obj o, boolean listInLobby) {		
		ArrayList<String> hrefs = new ArrayList<String>();
		if(o.getParent() == null && !o.getHref().isAbsolute()){	
			o.setHref(new Uri("http://localhost" + (o.getHref().toString().startsWith("/")?"":"/") + o.getHref().toString()));
		}

		String href = o.getFullContextPath();
		// don't add object if href is already assigned
		if(_objects.containsKey(href)){
			log.log(Level.WARNING,"Object with href: " + href + " already registered.");
			return null;
		}
		hrefs.add(href);
		_objects.put(href, o);
		
		synchronized(orderedObjects){
			orderedObjects.add(o);
		}

		// add root objects (objects without parent to the KNX lobby)
		if (listInLobby && !(o instanceof LobbyImpl) && o.getParent() == null) {
			Ref r = new Ref();
			r.setName(o.getName());
			r.setIs(ContractRegistry.lookupContract(o.getClass()));
			r.setHref(new Uri(o.getFullContextPath()));
			iotLobby.addReference(href, r);			
		}

		// add kids
		if (o.size() > 0) {
			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++)
				if (kids[i].getHref() != null)
					hrefs.addAll(addObj(kids[i]));
		}
		
		return hrefs;
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#removeObj(obix.Uri)
	 */
	@Override
	public void removeObj(String href) {
		
		_objects.remove(href);
		
		Obj toRemove = null;
		
		synchronized(orderedObjects){
			for(Obj obj :  orderedObjects){
				if(obj.getHref().equals(href)){
					toRemove = obj;
				}
			}
		}
		
		orderedObjects.remove(toRemove);
		objectRefresher.removeObject(toRemove);
		
		iotLobby.removeReference(href);		
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#invokeOp(obix.Uri, obix.Obj, boolean)
	 */
	@Override
	public Obj invokeOp(Uri uri, Obj input, boolean b) {	
		if (operationHandler.get(uri.toString()) != null) {
			return operationHandler.get(uri.toString()).invoke(input);
		}
		return new Err("No handler for operation defined.");
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#addOperationHandler(obix.Uri, at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.OperationHandler)
	 */
	@Override
	public void addOperationHandler(Uri uri, OperationHandler handler) {		
		operationHandler.put(uri.toString(), handler);
	}

	/* (non-Javadoc)
	 * @see at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBroker#getCoRELinks()
	 */
	@Override
	public String getCoRELinks() {

		StringBuffer coreLinks = new StringBuffer("");

		synchronized(orderedObjects){
			Iterator<Obj> objs = orderedObjects.iterator();
			while (objs.hasNext()) {
				Obj obj = objs.next();
				if (obj.getFullContextPath().startsWith("/")) {
					coreLinks.append("<" + obj.getFullContextPath() + ">;rt=\""
							+ ContractRegistry.lookupContract(obj.getClass())
							+ "\";if=\"obix\"");
				}
			}
		}

		return coreLinks.toString();
	}

	public static ObjectBroker getInstance() {
		return instance;
	}

	@Override
	public void addHistoryToDatapoints(Obj obj) {
		HistoryHelper.addHistoryToDatapoints(obj);		
	}

	@Override
	public void addHistoryToDatapoints(Obj obj, int countMax) {
		HistoryHelper.addHistoryToDatapoints(obj, countMax);	
	}

	@Override
	public void enableObjectRefresh(Obj obj) {
		objectRefresher.addObject(obj);	
	}

	@Override
	public void disableObjectRefresh(Obj obj) {
		objectRefresher.removeObject(obj);
	}
	
	@Override
	public void shutdown(){
		objectRefresher.stop();
	}

}
