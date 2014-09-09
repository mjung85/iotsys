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

import java.util.TimeZone;

import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotContractObject;
import com.google.appengine.api.iotsys.object.IotObject;

public class IotHistoryRecord extends IotContractObject implements Comparable<IotHistoryRecord>{

	public static final String CONTRACT = "obix:HistoryRecord";
	
	private static final long serialVersionUID = 1L;

	private IotAbsoluteTime timestamp;
	private IotObject value;
	
	public void setTimestamp(IotAbsoluteTime time) {
		this.children.remove(timestamp);
		this.timestamp = time;
		this.addChild(timestamp);
	}
	
	public IotAbsoluteTime getTimestamp() {
		return timestamp;
	}

	public TimeZone getTimeZone() {
		return timestamp.getTimeZone();
	}
	
	public void setValue(IotObject value) {
		this.children.remove(this.value);
		this.value = value;
		this.addChild(this.value);
	}

	public IotObject getValue() {
		return value;
	}

	@Override
	public void postInit() {
		for(IotObject child : this.getAllChildren()) {
			if(child.getClass().equals(IotAbsoluteTime.class)) {
				IotAbsoluteTime tdp = (IotAbsoluteTime) child;
				timestamp = tdp;
			} else {
				value = child;
			}
		}
		if(timestamp == null) {
			throw new IllegalArgumentException("timestamp datapoint not defined");
		}
		if(value == null) {
			throw new IllegalArgumentException("value object not defined");
		}
	}
	
	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}

	@Override
	public int compareTo(IotHistoryRecord other) {
		return this.getTimestamp().compareTo(other.getTimestamp());
	}
}
