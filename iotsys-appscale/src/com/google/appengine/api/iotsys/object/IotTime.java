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

import obix.Obj;
import obix.Time;

public class IotTime extends IotCalendarTypeDatapoint {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void initialize(Obj obj) {
		if (!obj.isTime()) {
			throw new IllegalArgumentException("obj is not of type Time");
		}
		super.initialize(obj);
		Time time = (Time) obj;
		Calendar calendar = getCalendar(time.getTz(), time.getHour(),
				time.getMinute(), time.getSecond(), time.getMillisecond());
		this.setValue(calendar);

		Time min = time.getMin();
		if (min != null) {
			calendar = getCalendar(min.getTz(), min.getHour(), min.getMinute(),
					min.getSecond(), min.getMillisecond());
			this.setMin(calendar);
		}

		Time max = time.getMax();
		if (max != null) {
			calendar = getCalendar(max.getTz(), max.getHour(), max.getMinute(),
					max.getSecond(), max.getMillisecond());
			this.setMax(calendar);
		}
	}

	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if (!obj.isTime()) {
			return;
		}
		Time time = (Time) obj;
		int hour = 0;
		int minute = 0;
		int second = 0;
		int millisecond = 0;
		if(this.getValue() != null) {
			hour = getCalendarValue(Calendar.HOUR_OF_DAY);
			minute = getCalendarValue(Calendar.MINUTE);
			second = getCalendarValue(Calendar.SECOND);
			millisecond = getCalendarValue(Calendar.MILLISECOND);
			time.set(hour, minute, second, millisecond);
			time.setTz(this.getValue().getTimeZone().getID());
		}
		if(this.getMin() != null) {
			Time min = new Time();
			hour = this.getMin().get(Calendar.HOUR_OF_DAY);
			minute = this.getMin().get(Calendar.MINUTE);
			second = this.getMin().get(Calendar.SECOND);
			millisecond = this.getMin().get(Calendar.MILLISECOND);
			min.set(hour, minute, second, millisecond);
			min.setTz(this.getValue().getTimeZone().getID());
			time.setMin(min);
		}
		if(this.getMax() != null) {
			Time max = new Time();
			hour = this.getMax().get(Calendar.HOUR_OF_DAY);
			minute = this.getMax().get(Calendar.MINUTE);
			second = this.getMax().get(Calendar.SECOND);
			millisecond = this.getMax().get(Calendar.MILLISECOND);
			max.set(hour, minute, second, millisecond);
			max.setTz(this.getValue().getTimeZone().getID());
			time.setMax(max);
		}
	}
	
}
