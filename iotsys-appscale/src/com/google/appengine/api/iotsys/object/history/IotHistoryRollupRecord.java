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
import com.google.appengine.api.iotsys.object.IotReal;

public class IotHistoryRollupRecord extends IotContractObject {

	private static final long serialVersionUID = 1L;
	
	public static final String CONTRACT = "obix:HistoryRollupRecord";

	private IotAbsoluteTime start;
	private IotAbsoluteTime end;
	private IotInteger count;
	private IotReal min;
	private IotReal max;
	private IotReal avg;
	private IotReal sum;
	
	public IotAbsoluteTime getStart() {
		return start;
	}
	
	public IotAbsoluteTime getEnd() {
		return end;
	}
	
	public long getCount() {
		return count.getValue();
	}
	
	public double getMin() {
		return min.getValue();
	}
	
	public double getMax() {
		return max.getValue();
	}
	
	public double getAverage() {
		return avg.getValue();
	}
	
	public double getSum() {
		return sum.getValue();
	}
	
	@Override
	public void postInit() {
		IotObject child = this.getChild("start");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("start datapoint not defined");
		}
		start = (IotAbsoluteTime) child;
		
		child = this.getChild("end");
		if(!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("end datapoint not defined");
		}
		end = (IotAbsoluteTime) child;
		
		child = this.getChild("count");
		if(!(child instanceof IotInteger)) {
			throw new IllegalArgumentException("count datapoint not defined");
		}
		count = (IotInteger) child;
		
		child = this.getChild("min");
		if(!(child instanceof IotReal)) {
			throw new IllegalArgumentException("min datapoint not defined");
		}
		min = (IotReal) child;
		
		child = this.getChild("max");
		if(!(child instanceof IotReal)) {
			throw new IllegalArgumentException("max datapoint not defined");
		}
		max = (IotReal) child;
		
		child = this.getChild("avg");
		if(!(child instanceof IotReal)) {
			throw new IllegalArgumentException("avg datapoint not defined");
		}
		avg = (IotReal) child;
		
		child = this.getChild("sum");
		if(!(child instanceof IotReal)) {
			throw new IllegalArgumentException("sum datapoint not defined");
		}
		sum = (IotReal) child;
	}
	
	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}

}
