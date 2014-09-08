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

import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotContractObject;
import com.google.appengine.api.iotsys.object.IotFeed;
import com.google.appengine.api.iotsys.object.IotInteger;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotOperation;

public class IotHistory extends IotContractObject {

	public static final String CONTRACT = "obix:History";

	private static final long serialVersionUID = 1L;

	private IotInteger count;
	private IotAbsoluteTime start;
	private IotAbsoluteTime end;
	private IotFeed feed;
	private IotOperation query;
	private IotOperation rollup;
	private IotOperation append;

	public IotInteger getCount() {
		return count;
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

	public IotFeed getFeed() {
		return feed;
	}

	public IotHistoryQueryOut query(long limit, long start, long end)
			throws CommunicationException {
		IotHistoryFilter filter = new IotHistoryFilter();
		filter.setLimit(limit);
		filter.setStart(start);
		filter.setEnd(end);
		return doQuery(filter);
	}

	public IotHistoryQueryOut query() throws CommunicationException {
		return doQuery(null);
	}

	private IotHistoryQueryOut doQuery(IotHistoryFilter filter)
			throws CommunicationException {
		IotObject result = query.invoke(filter);
		if (result instanceof IotHistoryQueryOut) {
			return (IotHistoryQueryOut) result;
		}
		throw new CommunicationException("expected IotHistoryQueryOut, got: "
				+ result);
	}

	public IotHistoryRollupOut rollup(long limit, long start, long end,
			long interval) throws CommunicationException {
		IotHistoryRollupIn rollupIn = new IotHistoryRollupIn();
		rollupIn.setLimit(limit);
		rollupIn.setStart(start);
		rollupIn.setEnd(end);
		rollupIn.setInterval(interval);
		return doRollup(rollupIn);
	}

	public IotHistoryRollupOut rollup() throws CommunicationException {
		return doRollup(null);
	}

	public IotHistoryRollupOut doRollup(IotHistoryRollupIn rollupIn)
			throws CommunicationException {
		IotObject result = rollup.invoke(rollupIn);
		if (result instanceof IotHistoryRollupOut) {
			return (IotHistoryRollupOut) result;
		}
		throw new CommunicationException("expected IotHistoryRollupOut, got: "
				+ result);
	}
	
	public IotHistoryAppendOut append(List<IotHistoryRecord> data) throws CommunicationException {
		IotHistoryAppendIn appendIn = new IotHistoryAppendIn();
		appendIn.setData(data);
		return doAppend(appendIn);
	}
	
	public IotHistoryAppendOut doAppend(IotHistoryAppendIn appendIn) throws CommunicationException {
		IotObject result = append.invoke(appendIn);
		if (result instanceof IotHistoryAppendOut) {
			return (IotHistoryAppendOut) result;
		}
		throw new CommunicationException("expected IotHistoryAppendOut, got: "
				+ result);
	}

	@Override
	public void postInit() {
		IotObject child = this.getChild("count");
		if (!(child instanceof IotInteger)) {
			throw new IllegalArgumentException("count datapoint not defined");
		}
		count = (IotInteger) child;

		child = this.getChild("start");
		if (!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("start datapoint not defined");
		}
		start = (IotAbsoluteTime) child;

		child = this.getChild("end");
		if (!(child instanceof IotAbsoluteTime)) {
			throw new IllegalArgumentException("end datapoint not defined");
		}
		end = (IotAbsoluteTime) child;

		child = this.getChild("feed");
		if (!(child instanceof IotFeed)) {
			throw new IllegalArgumentException("feed datapoint not defined");
		}
		feed = (IotFeed) child;

		child = this.getChild("query");
		if (!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("query operation not defined");
		}
		query = (IotOperation) child;

		child = this.getChild("rollup");
		if (!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("rollup operation not defined");
		}
		rollup = (IotOperation) child;

		child = this.getChild("append");
		if (!(child instanceof IotOperation)) {
			throw new IllegalArgumentException("append operation not defined");
		}
		append = (IotOperation) child;
	}

	@Override
	protected String getObjectContract() {
		return CONTRACT;
	}

}
