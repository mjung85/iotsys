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

package at.ac.tuwien.auto.iotsys.commons.obix.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ektorp.Page;

import at.ac.tuwien.auto.iotsys.commons.persistent.HistoryDbImpl;
import at.ac.tuwien.auto.iotsys.commons.persistent.models.DbHistoryFeed;
import at.ac.tuwien.auto.iotsys.obix.FeedFilter;
import obix.Abstime;
import obix.Bool;
import obix.Feed;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Str;
import obix.contracts.HistoryFilter;

public class HistoryFilterImpl extends Obj implements HistoryFilter, FeedFilter {
	
	private Int limit = new Int();
	private Abstime start = new Abstime();
	private Abstime end = new Abstime();
	
	public static final String HISTORY_FILTER_CONTRACT = "obix:HistoryFilter";
	
	public HistoryFilterImpl() {
		add(limit);
		add(start);
		add(end);
	}
	
	public HistoryFilterImpl(Obj filter) {
		this();
		
		if (filter instanceof HistoryFilter) {
			HistoryFilter histFilter = (HistoryFilter) filter;
			limit.set(histFilter.limit());
			start.set(histFilter.start());
			end.set(histFilter.end());
			
			limit.setNull(histFilter.limit().isNull());
			start.setNull(histFilter.start().isNull());
			end.setNull(histFilter.end().isNull());
		}
	}
	
	public Int limit() {
		return limit;
	}

	public Abstime start() {
		return start;
	}

	public Abstime end() {
		return end;
	}

	
	@Override
	public List<Obj> query(Feed feed) {
		ArrayList<HistoryRecordImpl> records = filterRecords(feed.getEvents());
		
		while (limit.get() > 0 & records.size() > limit.get())
			records.remove(records.size()-1);
		
		return new ArrayList<Obj>(records);
	}

	@Override
	public List<Obj> poll(List<Obj> events) {
		ArrayList<HistoryRecordImpl> records = filterRecords(events);
		Collections.reverse(records);
		
		while (limit.get() > 0 & records.size() > limit.get())
			records.remove(records.size()-1);
		
		return new ArrayList<Obj>(records);
	}
	
	
	private ArrayList<HistoryRecordImpl> filterRecords(List<Obj> events) {
		ArrayList<HistoryRecordImpl> filteredRecords = new ArrayList<HistoryRecordImpl>();

		// sort records by time stamp
		Collections.sort(events, new Comparator<Obj>() {
			public int compare(Obj obj1, Obj obj2) {
				HistoryRecordImpl r1 = (HistoryRecordImpl) obj1;
				HistoryRecordImpl r2 = (HistoryRecordImpl) obj2;
				return r1.timestamp().compareTo(r2.timestamp());
			}
		});
		
		for (Obj event : events) {
			if (!(event instanceof HistoryRecordImpl))
				continue;
			
			HistoryRecordImpl record = (HistoryRecordImpl) event;
			
			if (start.get() != end.get()) {
				if (!start.isNull() && record.timestamp().get() < start.get()) {
					continue;
				}

				if (!end.isNull() && record.timestamp().get() > end.get()) {
					continue;
				}
			}

			filteredRecords.add(record);
		}
		
		return filteredRecords;
	}

	@Override
	public FeedFilter getFilter(Obj filter) {
		return new HistoryFilterImpl(filter);
	}

}
