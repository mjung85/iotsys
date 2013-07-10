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
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.logging.Logger;

import obix.Contract;
import obix.Obj;
import obix.Op;
import obix.Reltime;
import obix.Uri;
import obix.contracts.Watch;
import obix.contracts.WatchIn;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.OperationHandler;
import at.ac.tuwien.auto.iotsys.gateway.obix.observer.ObjObserver;

/**
 * Implements the watch logic, representing a per-client state object.
 * This watch subscribes to resources and keeps a list of updates that occurred
 * until the client retrieves them.
 */
public class WatchImpl extends Obj implements Watch {
	private static final Logger log = Logger.getLogger(WatchImpl.class.getName());
	
	private static int numInstance = 0;
	public static final String WATCH_IN_CONTRACT = "obix:WatchIn";
	public static final String WATCH_OUT_CONTRACT = "obix:WatchOut";
	public static final String WATCH_CONTRACT = "obix:Watch";
	public static final String OBIX_NIL = "obix:Nil";
	
	// holds an observer object for each obix object that is watched using the normalized href path as key.
	private final Hashtable <String, ObjObserver> observers = new Hashtable<String, ObjObserver>();

	private final Hashtable<String, Uri> observedObjects = new Hashtable<String, Uri>();
	
	public WatchImpl(final ObjectBroker broker){
		setIs(new Contract(WATCH_CONTRACT));
		add(add());
		add(remove());
		add(lease());
		add(pollChanges());
		add(pollRefresh());
		add(delete());
		this.setHref(new Uri("http://localhost/watch" + (numInstance++)));
		broker.addOperationHandler(new Uri(this.getNormalizedHref().getPath() + "/add"), new OperationHandler() {
			@Override
			public Obj invoke(Obj in) {
				// Perform add logic
				WatchOutImpl ret = new WatchOutImpl();
				
				if(in instanceof WatchIn) {
					WatchIn watchIn = (WatchIn) in;
					
					// If an attempt is made to add the same URI multiple times in the same WatchIn request,
					// then the server SHOULD only return the object once.
					// Therefore filter out duplicate URIs
					ArrayList<Uri> uris = new ArrayList<Uri>();
					for(Obj u : watchIn.get("hrefs").list()) {
						if (!uris.contains(u)) uris.add((Uri) u);
					}
					
					for(Uri uri : uris) {
						Obj o = broker.pullObj(uri);
						if(!observedObjects.containsKey(uri.get())) {
							observedObjects.put(uri.get(), uri);
	
							ObjObserver observer = new ObjObserver();
							observers.put(uri.getPath(), observer);
							
							o.attach(observer);
						}
						
						Obj obj = null;
						if (o.isErr()) {
							obj = o;
						} else if(o.isOp()) {
							obj = new obix.Err("Watching operations not supported");
						} else {
							try {
								obj = (Obj) o.clone();
								obj.setName(null, true);
							} catch (CloneNotSupportedException e) {
								log.info("Obj not clonable" + e.getMessage());
							}
						}
						
						obj.setHref(uri);
						ret.values().add(obj, false);
					}
				}						
				return ret;
			}			
		});
		
		broker.addOperationHandler(new Uri(this.getNormalizedHref().getPath() + "/remove"), new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				// Perform remove logic
				if(in instanceof WatchIn){
					WatchIn watchIn = (WatchIn) in;
	
					for(Obj u : watchIn.get("hrefs").list()){
						Uri uri = (Uri) u;

						ObjObserver observer = observers.get(uri.getPath());

						observedObjects.remove(uri.getPath());

						observers.remove(uri.getPath());
						Obj o = broker.pullObj(uri);
						o.detach(observer);
					}					
				}		

				return new NilImpl();
			}			
		});
		
		broker.addOperationHandler(new Uri(this.getNormalizedHref().getPath() + "/pollChanges"), new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				WatchOutImpl out = new WatchOutImpl();
				synchronized(observers) {
					// check for modified objects
					// NOTE pollChanges does not need to provide the events, only the latest state.
					
					
					for (String uri : observers.keySet()) {
						ObjObserver objObserver = observers.get(uri);
						LinkedList<Obj> events = objObserver.getEvents();
						if(events.size() > 0) {
							// needs to be an obix object
							Obj obj = (Obj) objObserver.getSubject();
							
							try {
								Obj outItem = (Obj) obj.clone();
								outItem.setName(null, true);
								outItem.setHref(new Uri(uri));
								out.values().add(outItem, false);
							} catch (CloneNotSupportedException e) {
								log.info("Obj not clonable" + e.getMessage());
							}
						}
					}
				}
				
				return out;
			}		
		});
		broker.addOperationHandler(new Uri(this.getNormalizedHref().getPath() + "/pollRefresh"), new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				WatchOutImpl out = new WatchOutImpl();
				// Perform refresh logic	
				// Get a list of being-observed URI; get the corresponding object; notify the observer --> performing an update
				synchronized(observers){
					for (String uri : observers.keySet()) {
						ObjObserver observer = observers.get(uri);
						Obj beingObservedObject = (Obj) observer.getSubject();
						beingObservedObject.notifyObservers();
						
						Obj outItem;
						try {
							outItem = (Obj) beingObservedObject.clone();
							outItem.setName(null, true);
							outItem.setHref(new Uri(uri));
							out.values().add(outItem, false);
						} catch (CloneNotSupportedException e) {
							log.info("Obj not clonable" + e.getMessage());
						}
						
						observer.getEvents();
					}
				}
				return out;
			}		
		});

		

		broker.addOperationHandler(new Uri(this.getNormalizedHref().getPath() + "/delete"), new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				// Perform delete logic
				for (ObjObserver observer : observers.values()){
					Obj beingObservedObject = (Obj) observer.getSubject();
					beingObservedObject.detach(observer);
					observers.remove(observer);
					observer = null;
				}
				numInstance = 0;
				broker.removeOperationHandler(new Uri(thisWatch().getNormalizedHref().getPath() + "/add"));
				broker.removeOperationHandler(new Uri(thisWatch().getNormalizedHref().getPath() + "/remove"));
				broker.removeOperationHandler(new Uri(thisWatch().getNormalizedHref().getPath() + "/pollChanges"));
				broker.removeOperationHandler(new Uri(thisWatch().getNormalizedHref().getPath() + "/pollRefresh"));
				broker.removeOperationHandler(new Uri(thisWatch().getNormalizedHref().getPath() + "/delete"));
				broker.removeObj(thisWatch().getHref().getPath());

				return new NilImpl();

			}		
		});
	}
	
	public Watch thisWatch(){
		return this;
	}

	public Reltime lease() {
		// TODO make lease time writeable
		return new Reltime("lease", 60 * 1000); // 1 minute by defaults, not writable
	}

	public Op add() {
		return new Op("add", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
	}

	public Op remove() {
		return new Op("remove", new Contract(WATCH_IN_CONTRACT), new Contract(OBIX_NIL));
	}

	public Op pollChanges() {
		return new Op("pollChanges", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
	}

	public Op pollRefresh() {	
		return new Op("pollRefresh", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
	}

	public Op delete() {
		return new Op("delete", new Contract(OBIX_NIL), new Contract(OBIX_NIL));
	}

}
