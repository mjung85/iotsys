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

package com.google.appengine.api.iotsys.dev;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import com.google.apphosting.api.IotsysServicePb;
import com.google.apphosting.api.IotsysServicePb.IotObjectType;

public class ProtobufConverter {
	
	private static final Map<IotObjectType, Class<? extends IotObject>> objectForType;
	private static final Map<Class<? extends IotObject>, IotObjectType> typeForObject;
	
	static {
		objectForType = new HashMap<IotObjectType, Class<? extends IotObject>>();
		
		objectForType.put(IotObjectType.OBJECT, IotObject.class);
		objectForType.put(IotObjectType.REFERENCE, IotReference.class);
		objectForType.put(IotObjectType.ERR, IotError.class);
		objectForType.put(IotObjectType.FEED, IotFeed.class);
		objectForType.put(IotObjectType.LIST, IotList.class);
		objectForType.put(IotObjectType.OPERATION, IotOperation.class);
		
		objectForType.put(IotObjectType.DATAPOINT_BOOLEAN, IotBoolean.class);
		objectForType.put(IotObjectType.DATAPOINT_INTEGER, IotInteger.class);
		objectForType.put(IotObjectType.DATAPOINT_REAL, IotReal.class);
		objectForType.put(IotObjectType.DATAPOINT_STRING, IotString.class);
		objectForType.put(IotObjectType.DATAPOINT_ENUM, IotEnum.class);
		objectForType.put(IotObjectType.DATAPOINT_ABS_TIME, IotAbsoluteTime.class);
		objectForType.put(IotObjectType.DATAPOINT_REL_TIME, IotRelativeTime.class);
		objectForType.put(IotObjectType.DATAPOINT_DATE, IotDate.class);
		objectForType.put(IotObjectType.DATAPOINT_TIME, IotTime.class);
		objectForType.put(IotObjectType.DATAPOINT_URI, IotUri.class);
		
		objectForType.put(IotObjectType.WATCH, IotWatch.class);
		objectForType.put(IotObjectType.WATCH_OUT, IotWatchOut.class);
		objectForType.put(IotObjectType.WATCH_IN, IotWatchIn.class);
		
		objectForType.put(IotObjectType.HISTORY, IotHistory.class);
		objectForType.put(IotObjectType.HISTORY_QUERY_OUT, IotHistoryQueryOut.class);
		objectForType.put(IotObjectType.HISTORY_RECORD, IotHistoryRecord.class);
		objectForType.put(IotObjectType.HISTORY_FILTER, IotHistoryFilter.class);
		objectForType.put(IotObjectType.HISTORY_ROLLUP_IN, IotHistoryRollupIn.class);
		objectForType.put(IotObjectType.HISTORY_ROLLUP_OUT, IotHistoryRollupOut.class);
		objectForType.put(IotObjectType.HISTORY_ROLLUP_RECORD, IotHistoryRollupRecord.class);
		objectForType.put(IotObjectType.HISTORY_APPEND_IN, IotHistoryAppendIn.class);
		objectForType.put(IotObjectType.HISTORY_APPEND_OUT, IotHistoryAppendOut.class);
				
		typeForObject = reverseMap(objectForType);
		
	}
	
	private static <K, V> Map<V, K> reverseMap(Map<K, V> original) {
		Map<V, K> reverse = new HashMap<V, K>();
		for(Entry<K, V> e : original.entrySet()) {
			reverse.put(e.getValue(), e.getKey());
		}
		return reverse;
	}
	
	public IotsysServicePb.IotObjectProto iotObjectToProtobuf(IotObject object) {
		IotsysServicePb.IotObjectProto.Builder builder = IotsysServicePb.IotObjectProto.newBuilder();
		object.writeToProtobuf(builder);	
		if(typeForObject.containsKey(object.getClass())) {
			builder.setType(typeForObject.get(object.getClass()));
		} else {
			builder.setType(IotObjectType.OBJECT);
		}	
	
		for(IotObject child : object.getAllChildren()) {
			IotsysServicePb.IotObjectProto childProto = iotObjectToProtobuf(child);
			builder.addChildren(childProto);
		}
		
		return builder.build();
	}
	
	public IotObject protobufToIotObject(IotsysServicePb.IotObjectProto proto) {
		IotObject object = null;
		if(objectForType.containsKey(proto.getType())) {
			try {
				object = objectForType.get(proto.getType()).newInstance();
			} catch (InstantiationException e) {
				object = new IotObject();
			} catch (IllegalAccessException e) {
				object = new IotObject();
			}
		} else {
			object = new IotObject();
		}
	
		object.initialize(proto);
		
		for(IotsysServicePb.IotObjectProto child : proto.getChildrenList()) {
			IotObject childObject = protobufToIotObject(child);
			object.addChild(childObject);
		}
		
		object.postInit();
		
		return object;
	}

}
