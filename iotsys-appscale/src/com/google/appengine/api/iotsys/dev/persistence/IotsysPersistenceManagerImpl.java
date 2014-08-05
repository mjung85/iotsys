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

package com.google.appengine.api.iotsys.dev.persistence;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;

public class IotsysPersistenceManagerImpl implements IotsysPersistenceManager {

	private static final String APP_NAME_PROPERTY = "iotsys";
	
	private static final String ENTITY_PROPERTY_TIMESTAMP = "timestamp";
	private static final String ENTITY_PROPERTY_OBJECT = "object";
	
	@Override
	public void persistHistoryRecord(String objectUri, IotHistoryRecord record) {
//		LocalRpcService.Status status = new LocalRpcService.Status();
//		DatastoreV3Pb.PutRequest request = DatastoreV3Pb.PutRequest.getDefaultInstance();
//		Entity entity = new Entity(objectUri);
//		entity.setProperty(ENTITY_PROPERTY_TIMESTAMP, record.getTimestamp().getValue().getTimeInMillis());
//		entity.setProperty(ENTITY_PROPERTY_OBJECT, record.getValue());
//		
//		EntityProto eProto = EntityProto.getDefaultInstance();
//		Reference key = new Reference();
//		key.setApp(APP_NAME_PROPERTY);
//		key.setNameSpace(objectUri);
//		eProto.setKey(key);
//		
//		request.addEntity(eProto);
//		
//		LocalDatastoreService dsService = new LocalDatastoreService();
//		dsService.put(status, request);
		Entity e = new Entity(objectUri, APP_NAME_PROPERTY);
		e.setProperty(ENTITY_PROPERTY_TIMESTAMP, record.getTimestamp().getValue().getTimeInMillis());
		e.setProperty(ENTITY_PROPERTY_OBJECT, record.getValue());
		DatastoreService dsService = DatastoreServiceFactory.getDatastoreService();
		dsService.put(e);
	}

	@Override
	public List<IotHistoryRecord> getHistoryRecords(String objectUri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteHistoryRecords(String objectUri) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasRecords(String objectUri) throws CommunicationException {
		// TODO Auto-generated method stub
		return false;
	}

}
