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

package at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import obix.*;

/**
 * Refreshes the provided objects within a specified interval.
 */

public class ObjectRefresher implements Runnable {
	private static final HashSet<Obj> objects = new HashSet<Obj>();
	
	private volatile boolean stop = false;

	@Override
	public void run() {
		while(!stop){
			Iterator<Obj> iterator = objects.iterator();
			while(iterator.hasNext()){
				Obj obj = iterator.next();
				
				if (obj.needsRefresh()) {
					obj.refreshObject();
					//ArrayList<Val> vals = new ArrayList<Val>();// obj.getValChilds();
					//obj.getValChilds(vals);
					//System.out.println(vals.toString());
//					for (Val v : vals)
//						System.out.println(v.getFullContextPath() + ": " + v.toString());
					// set refresh timestamp
					obj.setLastRefresh(System.currentTimeMillis());
				}
			}
			try {
				Thread.sleep(Refreshable.MIN_REFRESH_INTERVAL_MS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addObject(final Obj obj){		
		// avoid locking of calling thread if object refresh is ongoing and takes some time (e.g. timeout)
		Thread t = new Thread(){
			public void run(){
				synchronized(objects){
					objects.add(obj);
				}
			}
		};
		t.start();
	}
	
	public void removeObject(final Obj obj){
		// avoid locking of calling thread if object refresh is ongoing and takes some time (e.g. timeout)
		Thread t = new Thread(){
			public void run(){
				synchronized(objects){
					objects.add(obj);
				}
			}
		};
		t.start();
	}

	public void stop() {
		stop = true;
		
	}
	
}
