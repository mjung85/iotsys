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

public class IotDate extends IotCalendarTypeDatapoint {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void initialize(Obj obj) {
		if (!obj.isDate()) {
			throw new IllegalArgumentException("obj is not of type Date");
		}
		super.initialize(obj);
		obix.Date date = (obix.Date) obj;
		Calendar calendar = getCalendar(date.getTz(), date.getYear(),
				date.getMonth(), date.getDay());
		this.setValue(calendar);

		obix.Date min = date.getMin();
		if(min != null) {
			calendar = getCalendar(min.getTz(), min.getYear(), min.getMonth(),
					min.getDay());
			this.setMin(calendar);
		}

		obix.Date max = date.getMax();
		if(max != null) {
			calendar = getCalendar(max.getTz(), max.getYear(), max.getMonth(),
					max.getDay());
			this.setMax(calendar);
		}
	}

	@Override
	public void writeToObj(Obj obj) {
		super.writeToObj(obj);
		if (!obj.isDate()) {
			return;
		}
		obix.Date date = (obix.Date) obj;
		
		int year = getCalendarValue(Calendar.YEAR);
		int month = getCalendarValue(Calendar.MONTH);
		int day = getCalendarValue(Calendar.DAY_OF_YEAR);
		date.set(year, month+1, day);
		
		if(this.getMin() != null) {
			obix.Date min = new obix.Date();
			year = this.getMin().get(Calendar.YEAR);
			month = this.getMin().get(Calendar.MONTH);
			day = this.getMin().get(Calendar.DAY_OF_YEAR);
			min.set(year, month+1, day);
			date.setMin(min);
		}
		
		if(this.getMax() != null) {
			obix.Date max = new obix.Date();
			year = this.getMax().get(Calendar.YEAR);
			month = this.getMax().get(Calendar.MONTH);
			day = this.getMax().get(Calendar.DAY_OF_YEAR);
			max.set(year, month+1, day);
			date.setMax(max);
		}
	}

}
