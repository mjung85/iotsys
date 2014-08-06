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

package com.google.appengine.api.iotsys.object.watch;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.IotContractObject;
import com.google.appengine.api.iotsys.object.IotError;
import com.google.appengine.api.iotsys.object.IotList;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotOperation;
import com.google.appengine.api.iotsys.object.IotRelativeTime;

public class IotWatch extends IotContractObject {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(IotWatch.class.getName());

	public static final String CONTRACT = "obix:Watch";
	
	private static final String CONTRACT_WATCH_OUT = "obix:WatchOut";	
	private static final String CONTRACT_WATCH_IN_ITEM = "obix:WatchInItem";
	
	private IotOperation add;
	private IotOperation remove;
	private IotOperation pollChanges;
	private IotOperation pollRefresh;
	private IotOperation delete;
	private IotRelativeTime lease;
	
	public void addWatchObject(IotObject object) throws CommunicationException {
		logger.info("adding watch object: " + object);
		IotObject watchIn = createWatchIn(object);
		IotObject response = add.invoke(watchIn);		
		logger.info("response from add operation: " + response);		
		if (response instanceof IotError) {
			throw new CommunicationException(response.getName());
		}
	}

	public void removeWatchObject(IotObject object) throws CommunicationException {
		logger.info("removing watch object: " + object);
		IotObject watchIn = createWatchIn(object);
		IotObject response = remove.invoke(watchIn);		
		logger.info("response from remove operation: " + response);		
		if (response instanceof IotError) {
			throw new CommunicationException(response.getName());
		}
	}

	public List<IotObject> pollChanges() throws CommunicationException {
		IotObject response = pollChanges.invoke(null);
		if (response instanceof IotError) {
			throw new CommunicationException(response.getName());
		}
		return getWatchOutObjects(response);
	}

	public List<IotObject> pollRefresh() throws CommunicationException {
		IotObject response = pollRefresh.invoke(null);
		if (response instanceof IotError) {
			throw new CommunicationException(response.getName());
		}
		return getWatchOutObjects(response);
	}

	public void delete() throws CommunicationException {
		IotObject response = delete.invoke(null);
		if (response instanceof IotError) {
			throw new CommunicationException(response.getName());
		}
	}
	
	public IotRelativeTime getLeaseTime() {
		return lease;
	}

	public void setLeaseTime(long time) throws CommunicationException {
		lease.setValue(time);
		lease.write();
	}
	
	private List<IotObject> getWatchOutObjects(IotObject response) {
		if(response.getContract().contains(CONTRACT_WATCH_OUT)) {
			for(IotObject child : response.getChildren(IotList.class)) {
				IotList list = (IotList) child;
				if(CONTRACT_WATCH_IN_ITEM.equals(list.getElementType())) {
					return list.getAllChildren();
				}		
			}
		}
		return null;
	}
	
	private IotObject createWatchIn(IotObject object) {
		IotWatchIn watchIn = new IotWatchIn();
		watchIn.addItem(object);
		return watchIn;
	}
	
	@Override
	public void postInit() {
		IotObject child = this.getChild("add");
		if(!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("add operation not defined");
		}
		add = (IotOperation) child;
		
		child = this.getChild("remove");
		if(!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("remove operation not defined");
		}
		remove = (IotOperation) child;
		
		child = this.getChild("pollChanges");
		if(!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("pollChanges operation not defined");
		}
		pollChanges = (IotOperation) child;
		
		child = this.getChild("pollRefresh");
		if(!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("pollRefresh operation not defined");
		}
		pollRefresh = (IotOperation) child;
		
		child = this.getChild("delete");
		if(!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("delete operation not defined");
		}
		delete = (IotOperation) child;
		
		child = this.getChild("lease");
		if(!(child instanceof IotRelativeTime)) {
			throw new IllegalArgumentException("lease datapoint not defined");
		}
		lease = (IotRelativeTime) child;
	}
	
	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}
	
}
