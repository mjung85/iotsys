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
import java.util.HashMap;
import java.util.logging.Logger;

import obix.Contract;
import obix.ContractRegistry;
import obix.Err;
import obix.Obj;
import obix.Op;
import obix.Ref;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.AlarmSubjectImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.general.impl.LobbyImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.AboutImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.HistoryHelper;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.WatchImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.WatchServiceImpl;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommHelper;

public class ObjectBrokerImpl implements ObjectBroker {

	private static final Logger log = Logger.getLogger(ObjectBrokerImpl.class
			.getName());

	private final Obj rootObject;

	private LobbyImpl iotLobby = null;

	private WatchServiceImpl watchServiceImpl = null;
	
	private AlarmSubjectImpl alarmSubjectImpl = null;

	private AboutImpl aboutImpl = null;

	private final HashMap<String, String> ipv6Mapping = new HashMap<String, String>();

	private static final ObjectBroker instance = new ObjectBrokerImpl();

	static {
		((ObjectBrokerImpl) instance).initInternals();
	}

	private ObjectRefresher objectRefresher = new ObjectRefresher();

	private MdnsResolver resolver;

	private ObjectBrokerImpl() {
		rootObject = new Obj();
		rootObject.setHref(new Uri("http://localhost/"));
		
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
		addObj(iotLobby, false);
		addObj(watchServiceImpl);
		addObj(alarmSubjectImpl);
		addObj(aboutImpl, false); // About is added directly in lobby as local

//		Obj enums = new Obj();
//		enums.setName("enums");
//		enums.setHref(new Uri("enums"));
//
//		// compareType enum
//		List compareTypes = new List();
//
//		compareTypes.setIs(new Contract("obix:Range"));
//		compareTypes.setHref(new Uri("compareTypes"));
//		compareTypes.setName("compareTypes");
//
//		Obj eq = new Obj();
//		eq.setName("eq");
//		Obj gte = new Obj();
//		gte.setName("gte");
//		Obj gt = new Obj();
//		gt.setName("gt");
//
//		Obj lt = new Obj();
//		lt.setName("lt");
//
//		Obj lte = new Obj();
//		lte.setName("lte");
//
//		compareTypes.add(eq);
//		compareTypes.add(lt);
//		compareTypes.add(lte);
//		compareTypes.add(gt);
//		compareTypes.add(gte);
//
//		enums.add(compareTypes);
//
//		// operation type enums
//
//		List operationTypes = new List();
//
//		operationTypes.setIs(new Contract("obix:Range"));
//		operationTypes.setHref(new Uri("operationTypes"));
//		operationTypes.setName("operationTypes");
//
//		Obj opAdd = new Obj();
//		opAdd.setName(BinaryOperation.BIN_OP_ADD);
//
//		Obj opSub = new Obj();
//		opSub.setName(BinaryOperation.BIN_OP_SUB);
//
//		Obj opMul = new Obj();
//		opMul.setName(BinaryOperation.BIN_OP_MUL);
//
//		Obj opMod = new Obj();
//		opMod.setName(BinaryOperation.BIN_OP_MOD);
//
//		Obj opDiv = new Obj();
//		opDiv.setName(BinaryOperation.BIN_OP_DIV);
//
//		operationTypes.add(opAdd);
//		operationTypes.add(opSub);
//		operationTypes.add(opMul);
//		operationTypes.add(opDiv);
//		operationTypes.add(opMod);
//
//		enums.add(operationTypes);
//
//		// binary logic operations
//		// operation type enums
//
//		List logicOperationTypes = new List();
//
//		logicOperationTypes.setIs(new Contract("obix:Range"));
//		logicOperationTypes.setHref(new Uri("logicOperationTypes"));
//		logicOperationTypes.setName("logicOperationTypes");
//
//		Obj opAnd = new Obj();
//		opAnd.setName(LogicBinaryOperation.BIN_OP_AND);
//
//		Obj opOr = new Obj();
//		opOr.setName(LogicBinaryOperation.BIN_OP_OR);
//
//		Obj opXor = new Obj();
//		opXor.setName(LogicBinaryOperation.BIN_OP_XOR);
//
//		Obj opNand = new Obj();
//		opNand.setName(LogicBinaryOperation.BIN_OP_NAND);
//
//		Obj opNor = new Obj();
//		opNor.setName(LogicBinaryOperation.BIN_OP_NOR);
//
//		logicOperationTypes.add(opAnd);
//		logicOperationTypes.add(opOr);
//		logicOperationTypes.add(opXor);
//		logicOperationTypes.add(opNand);
//		logicOperationTypes.add(opNor);

//		enums.add(logicOperationTypes);

//		addObj(enums, true);
		
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
		Obj o = rootObject.getByHref(href);
		
		// if the object could not be found, return an error
		if (o == null) {
			Err error = new Err("Object not found");
			error.setIs(new Contract("obix:BadUriErr"));
			return error;
		}
		
		o.refreshObject();
		return o;
	}

	@Override
	public synchronized Obj pushObj(Uri href, Obj input, boolean isOp)
			throws Exception {

		Obj o = pullObj(href);

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
	public synchronized void addObj(Obj o, String ipv6Address) {
		addObj(o);
		
		try {
			// generate Inet6Address in format "/:::::::"
			Inet6Address generateIPv6Address = (Inet6Address) Inet6Address.getByName(ipv6Address);
			
			String href = o.getFullContextPath();
			ipv6Mapping.put(generateIPv6Address.toString(), href);
			if(resolver != null) {
                resolver.addToRecordDict(href, ipv6Address);
                resolver.registerDevice(href, o.getClass(), ipv6Address);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
	public synchronized void addObj(Obj o) {
		addObj(o, true);
	}

	@Override
	public synchronized void addObj(Obj o, boolean listInLobby) {
		Obj root = o.getRoot();
		if (root != rootObject) {
			rootObject.add(root, false);
		}
		
		if (listInLobby) {
			Ref ref = new Ref(null, new Uri(o.getFullContextPath()));
			ref.setIs(o.getIs());
			ref.setName(o.getName());
			iotLobby.addReference(o.getFullContextPath(), ref);
		}
	}

	@Override
	public synchronized void removeObj(String href) {
		Obj toRemove = pullObj(new Uri(href));
		toRemove.removeThis();

		iotLobby.removeReference(href);
		// TODO remove references to descendants of referenced object?
		
		// TODO deal with group comm objects.
	}

	@Override
	public synchronized Obj invokeOp(Uri uri, Obj input) {
		Obj obj = pullObj(uri);
		
		if (obj instanceof Op) {
			Op op = (Op) obj;
			if (op.getOperationHandler() != null)
				return op.getOperationHandler().invoke(input);
		}
		
		return new Err("No handler for operation defined.");
	}

	@Override
	public synchronized String getCoRELinks() {
		return getCoRELinks(rootObject).toString();
	}
	
	private StringBuffer getCoRELinks(Obj obj) {
		StringBuffer coreLinks = new StringBuffer("");
		if (obj.getHref() == null) return coreLinks;
		
		if (obj != rootObject && obj != iotLobby)
			coreLinks.append(String.format("<%s>;rt=\"%s\";if=\"obix\"",
					obj.getFullContextPath(),
					ContractRegistry.lookupContract(obj.getClass())));
		
		for (Obj child : obj.list()) {
			if (child.isRef()) continue;
			coreLinks.append(getCoRELinks(child));
		}
		
		return coreLinks;
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
	public void enableGroupComm(Obj obj) {
		GroupCommHelper.enableGroupCommForObject(obj);
	}
	
	@Override
	public MdnsResolver getMDnsResolver() {
		return resolver;
	}
	@Override
	public void setMdnsResolver(MdnsResolver resolver){
		this.resolver = resolver;
	}
	
	@Override
	public synchronized void enableObjectRefresh(Obj obj, long interval) {
		obj.setRefreshInterval(interval);
		
		objectRefresher.addObject(obj);
	}
}
