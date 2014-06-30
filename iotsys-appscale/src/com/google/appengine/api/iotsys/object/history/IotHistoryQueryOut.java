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

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotContractObject;
import com.google.appengine.api.iotsys.object.IotInteger;
import com.google.appengine.api.iotsys.object.IotList;
import com.google.appengine.api.iotsys.object.IotObject;


public class IotHistoryQueryOut extends IotContractObject {

	public static final String CONTRACT = "obix:HistoryQueryOut";
	
	private static final long serialVersionUID = 1L;
	
	private IotInteger count;
	private IotAbsoluteTime start;
	private IotAbsoluteTime end;
	
	private List<IotHistoryRecord> records;
	
	public int getCount() {
		if(count == null) {
			return -1;
		}
		return (int)(long)count.getValue();
	}

	public IotAbsoluteTime getStart() {
		return start;
	}

	public IotAbsoluteTime getEnd() {
		return end;
	}

	public TimeZone getTimeZone() {
		return start.getTimeZone();
	}

	public List<IotHistoryRecord> getRecords() {
		return records;
	}
	
	@Override
	public void postInit() {
		IotObject child = this.getChild("count");
		if(!(child instanceof IotInteger)) {
			throw new IllegalArgumentException("count datapoint not defined");
		}
		count = (IotInteger) child;
		
		child = this.getChild("start");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("start datapoint not defined");
		}
		start = (IotAbsoluteTime) child;
		
		child = this.getChild("end");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("end datapoint not defined");
		}
		end = (IotAbsoluteTime) child;
	
		
		for(IotObject o : this.getChildren(IotList.class)) {
			IotList list = (IotList) o;
			if(list.getElementType().contains(IotHistoryRecord.CONTRACT)) {
				records = new ArrayList<IotHistoryRecord>();
				for(IotObject record : list.getChildren(IotHistoryRecord.class)) {
					records.add((IotHistoryRecord)record);
				}
			}
		}
	}
	
	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}
	
}
