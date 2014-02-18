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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.google.apphosting.api.IotsysServicePb;

public class IotCalendarTypeDatapoint extends IotDatapoint<Calendar> {

private static final long serialVersionUID = 1L;
	
	private Calendar min;
	private Calendar max;
	
	public void setMin(Calendar min) {
		this.min = min;
	}
	
	public Calendar getMin() {
		return min;
	}
	
	public void setMax(Calendar max) {
		this.max = max;
	}
	
	public Calendar getMax() {
		return max;
	}
	
	public void setValue(long value) {
		this.setValue(getCalendar(value));
	}
	
	public long getValueInMillis() {
		if(this.getValue() != null) {
			return this.getValue().getTimeInMillis();
		}
		return -1;
	}
	
	public void setTimeZone(String timezone) {
		setTimeZone(TimeZone.getTimeZone(timezone));
	}
	
	public void setTimeZone(TimeZone timezone) {
		if(this.getValue() != null) {
			this.getValue().setTimeZone(timezone);
		}
	}
	
	public TimeZone getTimeZone() {
		if(getValue() != null) {
			return getValue().getTimeZone();
		}
		return TimeZone.getDefault();
	}
	
	@Override
	protected boolean merge(IotObject object) {
		boolean sup = super.merge(object);
		if(object instanceof IotCalendarTypeDatapoint) {
			IotCalendarTypeDatapoint other = (IotCalendarTypeDatapoint) object;
			this.setValue(other.getValue());
			this.setMin(other.getMin());
			this.setMax(other.getMax());
		} else {
			return false;
		}
		return true & sup;
	}
	
	@Override
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		super.initialize(protobuf);
		TimeZone tz = getTimeZone();
		if(protobuf.hasTimezone()) {
			tz = TimeZone.getTimeZone(protobuf.getTimezone());
		}
		if(protobuf.hasIntValue()) {
			this.setValue(getCalendar(tz, protobuf.getIntValue()));
		}
		if(protobuf.hasIntMin()) {
			this.setMin(getCalendar(tz, protobuf.getIntMin()));
		}
		if(protobuf.hasIntMax()) {
			this.setMax(getCalendar(tz, protobuf.getIntMax()));
		}
	}
	
	@Override
	public void writeToProtobuf(IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		super.writeToProtobuf(protoBuilder);
		if(this.getValue() != null) {
			protoBuilder.setIntValue(this.getValue().getTimeInMillis());
			protoBuilder.setTimezone(this.getValue().getTimeZone().getID());
		}
		if(this.getMin() != null) {
			protoBuilder.setIntMin(this.getMin().getTimeInMillis());
		}
		if(this.getMax() != null) {
			protoBuilder.setIntMax(this.getMax().getTimeInMillis());
		}
	}
	
	protected int getCalendarValue(int flag) {
		if(getValue() != null) {
			return getValue().get(flag);
		}
		return -1;
	}
	
	protected Calendar getCalendar(String timezone, int year, int month, int day) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(timezone));
		calendar.set(year, month - 1, day);
		return calendar;
	}
	
	protected Calendar getCalendar(String timezone, int hour, int minute, int second, int millisecond) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone(timezone));
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);	
		return calendar;
	}
	
	protected Calendar getCalendar(long time) {
		return getCalendar(getTimeZone(), time);
	}

	protected Calendar getCalendar(String timezone, long time) {
		return getCalendar(TimeZone.getTimeZone(timezone), time);
	}
	
	protected Calendar getCalendar(TimeZone timezone, long time) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeZone(timezone);
		calendar.setTimeInMillis(time);
		return calendar;
	}
	
}
