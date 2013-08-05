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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import java.util.ArrayList;

import obix.Abstime;
import obix.Contract;
import obix.Int;
import obix.Obj;
import obix.Uri;
import obix.contracts.HistoryAppendOut;

public class HistoryAppendOutImpl extends Obj implements HistoryAppendOut {
	
	public static final String HISTORY_APPENDOUT_CONTRACT = "obix:HistoryAppendOut";
	
	private Int numAdded = new Int();
	private Int newCount = new Int();
	private Abstime newStart = new Abstime();
	private Abstime newEnd = new Abstime();


	public HistoryAppendOutImpl(ArrayList<HistoryRecordImpl> newRecords, ArrayList<HistoryRecordImpl> historyRecords) {	
	
		setIs(new Contract(HISTORY_APPENDOUT_CONTRACT));
		
		numAdded.setName("numAdded");
		numAdded.setHref(new Uri("numAdded"));
		
		newCount.setName("newCount");
		newCount.setHref(new Uri("newCount"));
		
		newStart.setName("newStart");
		newStart.setHref(new Uri("newStart"));
		
		newEnd.setName("newEnd");
		newEnd.setHref(new Uri("newEnd"));
		
		newCount.setSilent(historyRecords.size());
		numAdded.setSilent(newRecords.size());
		
		if (historyRecords.size() == 0) {
			newStart.setNull(true);
			newEnd.setNull(true);
		} else {
			Abstime start = historyRecords.get(historyRecords.size()-1).timestamp();
			newStart.set(start.get(), start.getTimeZone());
			
			Abstime end = historyRecords.get(0).timestamp();
			newEnd.set(end.get(), end.getTimeZone());
		}
		
		add(numAdded);
		add(newCount);
		add(newStart);
		add(newEnd);
	}


	@Override
	public Int numAdded() {
		return numAdded;
	}


	@Override
	public Int newCount() {
		return newCount;
	}


	@Override
	public Abstime newStart() {
		return newStart;
	}


	@Override
	public Abstime newEnd() {
		return newEnd;
	}


}
