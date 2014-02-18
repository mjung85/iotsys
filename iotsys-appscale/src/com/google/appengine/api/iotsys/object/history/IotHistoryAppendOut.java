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

package com.google.appengine.api.iotsys.object.history;

import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotContractObject;
import com.google.appengine.api.iotsys.object.IotInteger;
import com.google.appengine.api.iotsys.object.IotObject;

public class IotHistoryAppendOut extends IotContractObject {

	private static final long serialVersionUID = 1L;

	public static final String CONTRACT = "obix:HistoryAppendOut";
	
	private IotInteger numAdded;
	private IotInteger newCount;
	private IotAbsoluteTime newStart;
	private IotAbsoluteTime newEnd;
	
	public long getNewAdded() {
		if(numAdded == null) {
			return -1;
		}
		return numAdded.getValue();
	}
	
	public long getNewCount() {
		if(newCount == null) {
			return -1;
		}
		return newCount.getValue();
	}
	
	public IotAbsoluteTime getNewStart() {
		return newStart;
	}
	
	public IotAbsoluteTime getNewEnd() {
		return newEnd;
	}
	
	@Override
	public void postInit() {
		IotObject child = this.getChild("numAdded");
		if(!(child instanceof IotInteger)) {
			throw new IllegalArgumentException("numAdded datapoint not defined");
		}
		numAdded = (IotInteger) child;
		
		child = this.getChild("newCount");
		if(!(child instanceof IotInteger)) {
			throw new IllegalArgumentException("newCount datapoint not defined");
		}
		newCount = (IotInteger) child;
		
		child = this.getChild("newStart");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("newStart datapoint not defined");
		}
		newStart = (IotAbsoluteTime) child;
		
		child = this.getChild("newEnd");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("newEnd datapoint not defined");
		}
		newEnd = (IotAbsoluteTime) child;
	}
	
	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}

}
