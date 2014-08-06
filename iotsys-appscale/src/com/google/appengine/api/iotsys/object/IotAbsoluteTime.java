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

package com.google.appengine.api.iotsys.object;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import obix.Abstime;
import obix.Obj;

public class IotAbsoluteTime extends IotCalendarTypeDatapoint implements Comparable<IotAbsoluteTime> {

	private static final long serialVersionUID = 1L;
	
	public void setValue(long value) {
		setValue(getCalendar(getTimeZone(), value));
	}
	
	public int getDay() {
		return getCalendarValue(Calendar.DAY_OF_MONTH);
	}
	
	public int getMonth() {
		int month = getCalendarValue(Calendar.MONTH);
		if(month < 0) {
			return month;
		}
		return month + 1;
	}
	
	public int getYear() {
		return getCalendarValue(Calendar.YEAR);
	}
	
	public int getHour() {
		return getCalendarValue(Calendar.HOUR_OF_DAY);
	}
	
	public int getMinute() {
		return getCalendarValue(Calendar.MINUTE);
	}
	
	public int getSecond() {
		return getCalendarValue(Calendar.SECOND);
	}
	
	public int getMillisecond() {
		return getCalendarValue(Calendar.MILLISECOND);
	}
	
	public String getTimestamp() {
		return getTimestamp("yyyy-MM-dd hh:mm:ss-S");
	}
	
	public String getTimestamp(String format) {
		if(getValue() != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			return formatter.format(getValue().getTime());
		}
		return null;
	}
	
	@Override
	public void initialize(Obj obj) {
		if(!obj.isAbstime()) {
			throw new IllegalArgumentException("obj is not of type Abstime");
		}
		super.initialize(obj);
		Abstime abstime = (Abstime) obj;
		this.setValue(getCalendar(abstime.getTimeZone() , abstime.get()));
		if(abstime.getMin() != null) {
			this.setMin(getCalendar(abstime.getTimeZone(), abstime.getMin().get()));
		}
		if(abstime.getMax() != null) {
			this.setMax(getCalendar(abstime.getTimeZone(), abstime.getMax().get()));
		}
	}
	
	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if(!obj.isAbstime()) {
			return;
		}
		Abstime abstime = (Abstime) obj;
		if(this.getValue() != null) {
			abstime.set(this.getValue().getTimeInMillis(), getTimeZone());
		}
		if(this.getMin() != null) {
			abstime.setMin(new Abstime(this.getMin().getTimeInMillis(), getTimeZone()));
		}
		if(this.getMax() != null) {
			abstime.setMax(new Abstime(this.getMax().getTimeInMillis(), getTimeZone()));
		}
	}

	@Override
	public int compareTo(IotAbsoluteTime other) {
		return this.getValue().compareTo(other.getValue());
	}
	
}
