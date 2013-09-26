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

import obix.Abstime;
import obix.Int;
import obix.Obj;
import obix.Real;
import obix.Uri;
import obix.contracts.HistoryRollupRecord;

public class HistoryRollupRecordImpl extends Obj implements HistoryRollupRecord{
	
	public static final String HISTORY_ROLLUPRECORD_CONTRACT = "obix:HistoryRollupRecord";
	
	private Abstime start = new Abstime();
	private Abstime end = new Abstime();
	
	private Int count = new Int(0);
	private Real min = new Real(0);
	private Real max = new Real(0);
	private Real avg = new Real(0);
	private Real sum = new Real(0);
	
	public HistoryRollupRecordImpl(){
		count.setName("count");
		count.setHref(new Uri("count"));
		add(count);
		
		start.setName("start");
		start.setHref(new Uri("start"));
		add(start);
		
		end.setName("end");
		end.setHref(new Uri("end"));
		add(end);
		
		min.setName("min");
		min.setHref(new Uri("min"));
		add(min);
		
		max.setName("max");
		max.setHref(new Uri("max"));
		add(max);
		
		avg.setName("avg");
		avg.setHref(new Uri("avg"));
		add(avg);
		
		sum.setName("sum");
		sum.setHref(new Uri("sum"));
		add(sum);
	}
	

	public Abstime start() {

		return start;
	}

	public Abstime end() {

		return end;
	}

	public Int count() {

		return count;
	}

	public Real min() {

		return min;
	}

	public Real max() {

		return max;
	}

	public Real avg() {

		return avg;
	}

	public Real sum() {

		return sum;
	}

}
