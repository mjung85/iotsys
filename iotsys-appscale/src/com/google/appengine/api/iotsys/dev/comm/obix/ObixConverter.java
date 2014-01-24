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

package com.google.appengine.api.iotsys.dev.comm.obix;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import obix.Abstime;
import obix.Bool;
import obix.Date;
import obix.Err;
import obix.Feed;
import obix.Int;
import obix.List;
import obix.Obj;
import obix.Op;
import obix.Real;
import obix.Ref;
import obix.Reltime;
import obix.Str;
import obix.Time;
import obix.Uri;

import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotBoolean;
import com.google.appengine.api.iotsys.object.IotDate;
import com.google.appengine.api.iotsys.object.IotEnum;
import com.google.appengine.api.iotsys.object.IotError;
import com.google.appengine.api.iotsys.object.IotFeed;
import com.google.appengine.api.iotsys.object.IotInteger;
import com.google.appengine.api.iotsys.object.IotList;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotOperation;
import com.google.appengine.api.iotsys.object.IotReal;
import com.google.appengine.api.iotsys.object.IotReference;
import com.google.appengine.api.iotsys.object.IotRelativeTime;
import com.google.appengine.api.iotsys.object.IotString;
import com.google.appengine.api.iotsys.object.IotTime;
import com.google.appengine.api.iotsys.object.IotUri;
import com.google.appengine.api.iotsys.object.history.IotHistory;
import com.google.appengine.api.iotsys.object.history.IotHistoryAppendIn;
import com.google.appengine.api.iotsys.object.history.IotHistoryAppendOut;
import com.google.appengine.api.iotsys.object.history.IotHistoryFilter;
import com.google.appengine.api.iotsys.object.history.IotHistoryQueryOut;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.api.iotsys.object.history.IotHistoryRollupIn;
import com.google.appengine.api.iotsys.object.history.IotHistoryRollupOut;
import com.google.appengine.api.iotsys.object.history.IotHistoryRollupRecord;
import com.google.appengine.api.iotsys.object.watch.IotWatch;
import com.google.appengine.api.iotsys.object.watch.IotWatchIn;
import com.google.appengine.api.iotsys.object.watch.IotWatchOut;

public class ObixConverter {

	private static final Map<Class<? extends Obj>, Class<? extends IotObject>> iotObjectforObj;
	private static final Map<Class<? extends IotObject>, Class<? extends Obj>> objForIotObject;

	private static final Map<String, Class<? extends IotObject>> iotObjectForContract;

	private static final Logger logger = Logger.getLogger(ObixConverter.class
			.getName());

	static {
		iotObjectforObj = new HashMap<Class<? extends Obj>, Class<? extends IotObject>>();

		iotObjectforObj.put(Obj.class, IotObject.class);
		iotObjectforObj.put(Ref.class, IotReference.class);
		iotObjectforObj.put(Err.class, IotError.class);
		iotObjectforObj.put(List.class, IotList.class);
		iotObjectforObj.put(Op.class, IotOperation.class);
		iotObjectforObj.put(Feed.class, IotFeed.class);

		iotObjectforObj.put(Bool.class, IotBoolean.class);
		iotObjectforObj.put(Int.class, IotInteger.class);
		iotObjectforObj.put(Real.class, IotReal.class);
		iotObjectforObj.put(Str.class, IotString.class);
		iotObjectforObj.put(obix.Enum.class, IotEnum.class);
		iotObjectforObj.put(Abstime.class, IotAbsoluteTime.class);
		iotObjectforObj.put(Reltime.class, IotRelativeTime.class);
		iotObjectforObj.put(Date.class, IotDate.class);
		iotObjectforObj.put(Time.class, IotTime.class);
		iotObjectforObj.put(Uri.class, IotUri.class);

		objForIotObject = reverseMap(iotObjectforObj);

		iotObjectForContract = new HashMap<String, Class<? extends IotObject>>();
		iotObjectForContract.put("obix:BadUriErr", IotError.class);
		
		iotObjectForContract.put(IotWatch.CONTRACT, IotWatch.class);
		iotObjectForContract.put(IotWatchOut.CONTRACT, IotWatchOut.class);
		iotObjectForContract.put(IotWatchIn.CONTRACT, IotWatchIn.class);

		iotObjectForContract.put(IotHistory.CONTRACT, IotHistory.class);
		iotObjectForContract.put(IotHistoryQueryOut.CONTRACT, IotHistoryQueryOut.class);
		iotObjectForContract.put(IotHistoryRecord.CONTRACT, IotHistoryRecord.class);
		iotObjectForContract.put(IotHistoryFilter.CONTRACT, IotHistoryFilter.class);
		iotObjectForContract.put(IotHistoryRollupIn.CONTRACT, IotHistoryRollupIn.class);
		iotObjectForContract.put(IotHistoryRollupOut.CONTRACT, IotHistoryRollupOut.class);
		iotObjectForContract.put(IotHistoryRollupRecord.CONTRACT, IotHistoryRollupRecord.class);
		iotObjectForContract.put(IotHistoryAppendIn.CONTRACT, IotHistoryAppendIn.class);
		iotObjectForContract.put(IotHistoryAppendOut.CONTRACT, IotHistoryAppendOut.class);
	}

	private static <K, V> Map<V, K> reverseMap(Map<K, V> original) {
		Map<V, K> reverse = new HashMap<V, K>();
		for (Entry<K, V> e : original.entrySet()) {
			reverse.put(e.getValue(), e.getKey());
		}
		return reverse;
	}

	public ObixConverter() {
	}

	public Obj iotObjectToObj(IotObject object) {
		Obj obj;
		if (objForIotObject.containsKey(object.getClass())) {
			try {
				obj = objForIotObject.get(object.getClass()).newInstance();
			} catch (InstantiationException e) {
				obj = new Obj();
			} catch (IllegalAccessException e) {
				obj = new Obj();
			}
		} else {
			obj = new Obj();
		}
		object.writeToObj(obj);

		for (IotObject child : object.getAllChildren()) {
			Obj childObj = iotObjectToObj(child);
			obj.add(childObj);
		}

		return obj;
	}

	public IotObject objToIotObject(Obj obj) {
		return objToIotObject(obj, null);
	}
	
	public IotObject objToIotObject(Obj obj, IotObject parent) {
		IotObject object = null;

		if(obj.isRef()) {
			object = new IotReference();
		}else if (obj.getIs() != null) {
			logger.info("object has contract: " + obj.getIs().toString());
			for (int i = 0; i < obj.getIs().size(); i++) {				
				object = instantiateByContract(obj.getIs().get(i).get());
			}
		} else if(parent instanceof IotList) {
			IotList parentList = (IotList) parent;
			if(parentList.getElementType() != null) {
				object = instantiateByContract(parentList.getElementType());
			}
		} else if(parent instanceof IotFeed) {
			IotFeed parentFeed = (IotFeed) parent;
			if(parentFeed.getElementType() != null) {
				object = instantiateByContract(parentFeed.getElementType());
			}
		} 

		if (object == null) {
			if (iotObjectforObj.containsKey(obj.getClass())) {
				Class<? extends IotObject> clazz = iotObjectforObj.get(obj
						.getClass());
				try {
					object = clazz.newInstance();
				} catch (InstantiationException e) {
					logger.warning("could not instantiate class "
							+ clazz.getSimpleName() + ": " + e.getMessage()
							+ "; using IotObjectImpl");
					object = new IotObject();
				} catch (IllegalAccessException e) {
					logger.warning("could not access class "
							+ clazz.getSimpleName() + ": " + e.getMessage()
							+ "; using IotObjectImpl");
					object = new IotObject();
				}
			} else {
				object = new IotObject();
			}
		}

		object.initialize(obj);
		for (Obj child : obj.list()) {
			IotObject childObject = objToIotObject(child, object);
			object.addChild(childObject);
		}
		
		object.postInit();
		
		return object;

	}
	
	private IotObject instantiateByContract(String contract) {
		IotObject object = null;
		if (iotObjectForContract.containsKey(contract)) {
			logger.info("object contract found in map: " + contract);
			Class<? extends IotObject> clazz = iotObjectForContract
					.get(contract);
			try {
				object = clazz.newInstance();
			} catch (InstantiationException e) {
				logger.warning("could not instantiate class "
						+ clazz.getSimpleName() + ": " + e.getMessage());
			} catch (IllegalAccessException e) {
				logger.warning("could not access class "
						+ clazz.getSimpleName() + ": " + e.getMessage());
			}
		}
		return object;
	}

}
