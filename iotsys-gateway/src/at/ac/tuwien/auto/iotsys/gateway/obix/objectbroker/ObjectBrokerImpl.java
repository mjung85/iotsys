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

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import obix.Contract;
import obix.ContractRegistry;
import obix.Err;
import obix.List;
import obix.Obj;
import obix.Ref;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.OperationHandler;
import at.ac.tuwien.auto.iotsys.commons.MDnsResolver;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AboutImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AlarmSubjectImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.HistoryHelper;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.WatchImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.WatchServiceImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.general.impl.LobbyImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.BinaryOperation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.LogicBinaryOperation;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommHelper;

public class ObjectBrokerImpl implements ObjectBroker {

	private static final Logger log = Logger.getLogger(ObjectBrokerImpl.class
			.getName());

	// href, obj
	private final HashMap<String, Obj> objects;

	private final HashMap<String, String> nameToHref;

	private LobbyImpl iotLobby = null;

	private WatchServiceImpl watchServiceImpl = null;
	
	private AlarmSubjectImpl alarmSubjectImpl = null;

	private AboutImpl aboutImpl = null;

	private final Hashtable<String, OperationHandler> operationHandler = new Hashtable<String, OperationHandler>();

	private final HashMap<String, String> ipv6Mapping = new HashMap<String, String>();

	private final ArrayList<Obj> orderedObjects = new ArrayList<Obj>();

	private static final ObjectBroker instance = new ObjectBrokerImpl();

	static {
		((ObjectBrokerImpl) instance).initInternals();
	}

	private ObjectRefresher objectRefresher = new ObjectRefresher();

	private MDnsResolver resolver;

	private ObjectBrokerImpl() {
		objects = new HashMap<String, Obj>();
		nameToHref = new HashMap<String, String>();
		iotLobby = new LobbyImpl();

		aboutImpl = new AboutImpl();

		watchServiceImpl = new WatchServiceImpl(this);
		alarmSubjectImpl = new AlarmSubjectImpl(this);
	}

	@Override
	public HashMap<String, String> get_ipv6MappingTable() {
		return ipv6Mapping;
	}

	private void initInternals() {
		addObj(watchServiceImpl);
		addObj(alarmSubjectImpl);
		addObj(aboutImpl, false); // About is added directly in lobby as local
		
		alarmSubjectImpl.initialize();
		removeObj(alarmSubjectImpl.getFullContextPath());

		Obj enums = new Obj();
		enums.setName("enums");
		enums.setHref(new Uri("enums"));

		// compareType enum
		List compareTypes = new List();

		compareTypes.setIs(new Contract("obix:Range"));
		compareTypes.setHref(new Uri("compareTypes"));
		compareTypes.setName("compareTypes");

		Obj eq = new Obj();
		eq.setName("eq");
		Obj gte = new Obj();
		gte.setName("gte");
		Obj gt = new Obj();
		gt.setName("gt");

		Obj lt = new Obj();
		lt.setName("lt");

		Obj lte = new Obj();
		lte.setName("lte");

		compareTypes.add(eq);
		compareTypes.add(lt);
		compareTypes.add(lte);
		compareTypes.add(gt);
		compareTypes.add(gte);

		enums.add(compareTypes);

		// operation type enums

		List operationTypes = new List();

		operationTypes.setIs(new Contract("obix:Range"));
		operationTypes.setHref(new Uri("operationTypes"));
		operationTypes.setName("operationTypes");

		Obj opAdd = new Obj();
		opAdd.setName(BinaryOperation.BIN_OP_ADD);

		Obj opSub = new Obj();
		opSub.setName(BinaryOperation.BIN_OP_SUB);

		Obj opMul = new Obj();
		opMul.setName(BinaryOperation.BIN_OP_MUL);

		Obj opMod = new Obj();
		opMod.setName(BinaryOperation.BIN_OP_MOD);

		Obj opDiv = new Obj();
		opDiv.setName(BinaryOperation.BIN_OP_DIV);

		operationTypes.add(opAdd);
		operationTypes.add(opSub);
		operationTypes.add(opMul);
		operationTypes.add(opDiv);
		operationTypes.add(opMod);

		enums.add(operationTypes);

		// binary logic operations
		// operation type enums

		List logicOperationTypes = new List();

		logicOperationTypes.setIs(new Contract("obix:Range"));
		logicOperationTypes.setHref(new Uri("logicOperationTypes"));
		logicOperationTypes.setName("logicOperationTypes");

		Obj opAnd = new Obj();
		opAnd.setName(LogicBinaryOperation.BIN_OP_AND);

		Obj opOr = new Obj();
		opOr.setName(LogicBinaryOperation.BIN_OP_OR);

		Obj opXor = new Obj();
		opXor.setName(LogicBinaryOperation.BIN_OP_XOR);

		Obj opNand = new Obj();
		opNand.setName(LogicBinaryOperation.BIN_OP_NAND);

		Obj opNor = new Obj();
		opNor.setName(LogicBinaryOperation.BIN_OP_NOR);

		logicOperationTypes.add(opAnd);
		logicOperationTypes.add(opOr);
		logicOperationTypes.add(opXor);
		logicOperationTypes.add(opNand);
		logicOperationTypes.add(opNor);

		enums.add(logicOperationTypes);

		addObj(enums, true);
		
		// create default watch
		WatchImpl watchImpl = new WatchImpl(this);	
		addObj(watchImpl);

		

//		// Static comperators
//
//		for (int i = 1; i <= 3; i++) {
//			ComparatorImpl comp = new ComparatorImpl();
//			comp.setName("comp" + i);
//			comp.setHref(new Uri("comp" + i));
//			addObj(comp);
//			enableGroupComm(comp);
//		}

//		// Static temperature controllers
//
//		for (int i = 1; i <= 3; i++) {
//			TemperatureControllerImpl tempControl = new TemperatureControllerImpl();
//			tempControl.setName("tempControl" + i);
//			tempControl.setHref(new Uri("tempControl" + i));
//
//			addObj(tempControl);
//			enableGroupComm(tempControl);
//		}
//
//		// Static binary operation
//
//		for (int i = 1; i <= 3; i++) {
//			BinaryOperationImpl binOperation = new BinaryOperationImpl();
//			binOperation.setName("binOp" + i);
//			binOperation.setHref(new Uri("binOp" + i));
//
//			addObj(binOperation);
//			enableGroupComm(binOperation);
//		}
//
//		// Static logic binary operation
//
//		for (int i = 1; i <= 3; i++) {
//			LogicBinaryOperationImpl logicBinOperation = new LogicBinaryOperationImpl();
//			logicBinOperation.setName("logicBinOp" + i);
//			logicBinOperation.setHref(new Uri("logicBinOp" + i));
//
//			addObj(logicBinOperation);
//			enableGroupComm(logicBinOperation);
//		}

		Thread t = new Thread(objectRefresher);
		t.start();
	}

	@Override
	public synchronized Obj pullObj(Uri href) {

		// if the path pointing to the lobby has been entered, the lobby is
		// returned
		String path = href.getPath();

		// oBIX lobby
		if (path.equals("/obix") || path.equals("/obix/"))
			return iotLobby;

		// else, the href references an internal object -> look it up in the
		// object database

		Obj o = objects.get(href.getPath());
		if (o != null) {
			o.refreshObject();
		}

		// if the object could not be found, return an error
		if (o == null) {
			Err error = new Err("Object not found");
			error.setIs(new Contract("obix:BadUriErr"));
			return error;
		}

		return o;
	}

	@Override
	public synchronized Obj pushObj(Uri href, Obj input, boolean isOp)
			throws Exception {

		Obj o = objects.get(href.getPath());

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

	@Override
	public synchronized ArrayList<String> addObj(Obj o, String ipv6Address) {

		try {
			// generate Inet6Address in format "/:::::::"
			Inet6Address generateIPv6Address = (Inet6Address) Inet6Address
					.getByName(ipv6Address);

			if (o.getParent() == null && !o.getHref().isAbsolute()) { // root
																		// obj
																		// need
																		// to
																		// have
																		// an
																		// absolute
																		// URL
				o.setHref(new Uri("http://localhost"
						+ (o.getHref().toString().startsWith("/") ? "" : "/")
						+ o.getHref().toString()));
			}
			String href = o.getFullContextPath();
			ipv6Mapping.put(generateIPv6Address.toString(), href);
//			System.out.println("href: " + href + " ipv6: " + ipv6Address);
			if(resolver != null)
				resolver.addToRecordDict(href, ipv6Address);

			// add kids 
			//TODO this should be done recursively, not only for the first level of kids
			if (o.size() > 0) {
				Obj[] kids = o.list();
				for (int i = 0; i < o.size(); i++) {
					if (kids[i].getHref() != null) {
						ipv6Mapping.put(generateIPv6Address.toString() + "/"
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

	@Override
	public synchronized String getIPv6LinkedHref(String ipv6Address) {
		return ipv6Mapping.get(ipv6Address);
	}

	@Override
	public synchronized boolean containsIPv6(String ipv6Address) {
		return ipv6Mapping.containsKey(ipv6Address);
	}

	@Override
	public synchronized ArrayList<String> addObj(Obj o) {
		return addObj(o, true);
	}

	@Override
	public synchronized ArrayList<String> addObj(Obj o, boolean listInLobby) {
		ArrayList<String> hrefs = new ArrayList<String>();
		if (o.getParent() == null && !o.getHref().isAbsolute()) {
			o.setHref(new Uri("http://localhost"
					+ (o.getHref().toString().startsWith("/") ? "" : "/")
					+ o.getHref().toString()));
		}

		String href = o.getFullContextPath();
		// don't add object if href is already assigned
		if (objects.containsKey(href)) {
			log.log(Level.WARNING, "Object with href: " + href
					+ " already registered.");
			return hrefs;
		}
		hrefs.add(href);
		objects.put(href, o);

		orderedObjects.add(o);

		// add root objects (objects without parent to the KNX lobby)
		if (listInLobby && !(o instanceof LobbyImpl) && o.getParent() == null) {
			Ref r = new Ref();
			r.setName(o.getName());
			r.setIs(ContractRegistry.lookupContract(o.getClass()));
			r.setHref(new Uri(o.getFullContextPath()));
			iotLobby.addReference(href, r);

			// also allow to query them by name
			nameToHref.put(o.getName(), href);
		}

		// add kids
		if (o.size() > 0) {
			Obj[] kids = o.list();
			for (int i = 0; i < o.size(); i++)
				if (kids[i].getHref() != null){
					try {
					hrefs.addAll(addObj(kids[i]));
					}
					catch(Exception e){
						// TODO issue
					}
				}
			// FIXME: should we store kid's href as absolute rather than relative href?
		}

		return hrefs;
	}

	@Override
	public synchronized void removeObj(String href) {
		Obj toRemove = objects.get(href);
		objects.remove(href);

		orderedObjects.remove(toRemove);
		objectRefresher.removeObject(toRemove);

		if (toRemove.getName() != null) {
			nameToHref.remove(toRemove.getName());
		}

		iotLobby.removeReference(href);

		// TODO deal with group comm objects.
	}

	@Override
	public synchronized Obj invokeOp(Uri uri, Obj input, boolean b) {
		if (operationHandler.get(uri.toString()) != null) {
			return operationHandler.get(uri.toString()).invoke(input);
		}
		return new Err("No handler for operation defined.");
	}

	@Override
	public synchronized void addOperationHandler(Uri uri,
			OperationHandler handler) {
		operationHandler.put(uri.toString(), handler);
	}
	
	@Override
	public void removeOperationHandler(Uri uri){
		operationHandler.remove(uri.getPath());
	}

	@Override
	public synchronized String getCoRELinks() {

		StringBuffer coreLinks = new StringBuffer("");

		Iterator<Obj> objs = orderedObjects.iterator();
		while (objs.hasNext()) {
			Obj obj = objs.next();
			if (obj.getFullContextPath().startsWith("/")) {
				coreLinks.append("<" + obj.getFullContextPath() + ">;rt=\""
						+ ContractRegistry.lookupContract(obj.getClass())
						+ "\";if=\"obix\"");
			}
		}

		return coreLinks.toString();
	}

	public static ObjectBroker getInstance() {
		return instance;
	}

	@Override
	public synchronized void addHistoryToDatapoints(Obj obj) {
		HistoryHelper.addHistoryToDatapoints(obj);
	}

	@Override
	public synchronized void addHistoryToDatapoints(Obj obj, int countMax) {
		HistoryHelper.addHistoryToDatapoints(obj, countMax);
	}

	@Override
	public synchronized void enableObjectRefresh(Obj obj) {
		objectRefresher.addObject(obj);
	}

	@Override
	public synchronized void disableObjectRefresh(Obj obj) {
		objectRefresher.removeObject(obj);
	}

	@Override
	public synchronized void shutdown() {
		objectRefresher.stop();
	}

	@Override
	public synchronized Obj pullObByName(String name) {
		String href = nameToHref.get(name);
		if (href != null) {
			return objects.get(href);
		}
		return null;
	}

	@Override
	public synchronized ArrayList<String> getObjNames() {

		ArrayList<String> ret = new ArrayList<String>();
		for (String name : nameToHref.keySet()) {
			if (name != null && name.length() > 0) {
				ret.add(name);
			}
		}
		return ret;
	}

	@Override
	public void enableGroupComm(Obj obj) {
		GroupCommHelper.enableGroupCommForObject(obj);
	}
	
	@Override
	public MDnsResolver getMDnsResolver() {
		return resolver;
	}
	@Override
	public void setMdnsResolver(MDnsResolver resolver){
		this.resolver = resolver;
	}
}
