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
import java.util.List;

import obix.Abstime;
import obix.Feed;
import obix.Obj;
import obix.contracts.HistoryFilter;

public class HistoryFeed extends Feed {
	
	public HistoryFeed() {
		this(HistoryHelper.HISTORY_COUNT_DEFAULT);
	}
	
	public HistoryFeed(int maxHistoryCount) {
		setMaxEvents(maxHistoryCount);
	}
	
	@Override
	public List<Obj> query(List<Obj> events, Obj filter) {
		if (!(filter instanceof HistoryFilter))
			return new ArrayList<Obj>(events);
		
		HistoryFilter in = (HistoryFilter) filter;
		return new ArrayList<Obj>(filterRecords(events, in));
	}
	
	public ArrayList<HistoryRecordImpl> getRecords() {
		ArrayList<HistoryRecordImpl> records = new ArrayList<HistoryRecordImpl>();
		for (Obj event : getEvents()) {
			if (event instanceof HistoryRecordImpl)
				records.add((HistoryRecordImpl) event);
		}
		
		return records;
	}
	
	public ArrayList<HistoryRecordImpl> filterRecords(List<Obj> events, HistoryFilter historyFilter) {
		long limit = 0;
		Abstime start = new Abstime();
		Abstime end = new Abstime();
		
		if (historyFilter != null) {
			limit = historyFilter.limit().get();
			start = historyFilter.start();
			end = historyFilter.end();
		}

		ArrayList<HistoryRecordImpl> filteredRecords = new ArrayList<HistoryRecordImpl>();
		
		for (Obj event : events) {
			if (!(event instanceof HistoryRecordImpl))
				continue;
			
			HistoryRecordImpl record = (HistoryRecordImpl) event;
			boolean addRecord = true;

			if (limit != 0) { // unlimited
				if (filteredRecords.size() + 1 > limit) {
					break;
				}
			}

			if (start.get() != end.get()) {
				if (start != null && start.get() != 0
						&& record.timestamp().get() < start.get()) {
					addRecord = false;
				}

				if (end != null && end.get() != 0
						&& record.timestamp().get() > end.get()) {
					addRecord = false;
				}
			}

			if (addRecord) {
				filteredRecords.add(record);
			}
		}
		return filteredRecords;
	}
}
