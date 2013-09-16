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
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import obix.Contract;
import obix.Feed;
import obix.IObj;
import obix.Obj;
import obix.Op;
import obix.Reltime;
import obix.Uri;
import obix.contracts.Watch;
import obix.contracts.WatchIn;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.observer.FeedObserver;
import at.ac.tuwien.auto.iotsys.obix.FeedFilter;
import at.ac.tuwien.auto.iotsys.obix.OperationHandler;
import at.ac.tuwien.auto.iotsys.obix.observer.EventObserver;
import at.ac.tuwien.auto.iotsys.obix.observer.ObjObserver;
import at.ac.tuwien.auto.iotsys.obix.observer.Observer;

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
	
	private static ScheduledThreadPoolExecutor timers = new ScheduledThreadPoolExecutor(1);
	private ScheduledFuture<?> expirationTimer;
	private Runnable expireTask;
	
	private ObjectBroker broker;
	
	// holds an observer object for each obix object that is watched using the normalized href path as key.
	private final Hashtable <String, EventObserver<Obj>> observers = new Hashtable<String, EventObserver<Obj>>();

	private final Hashtable<String, Uri> observedObjects = new Hashtable<String, Uri>();
	
	private static final int DEFAULT_LEASE = 60 * 1000; // 1 minute
	private Reltime lease;
	
	public WatchImpl(final ObjectBroker broker) {
		this.broker = broker;
		
		setIs(new Contract(WATCH_CONTRACT));
		setHref(new Uri("watch" + (numInstance++)));
		
		add(add());
		add(remove());
		add(lease());
		add(pollChanges());
		add(pollRefresh());
		add(delete());
		
		resetExpiration();
	}
	
	public Watch thisWatch(){
		return this;
	}

	public Reltime lease() {
		if (lease == null) {
			lease = new Reltime("lease", DEFAULT_LEASE);
			lease.setHref(new Uri("lease"));
			lease.setWritable(true);
		}
		
		return lease;
	}

	public Op add() {
		Op add = new Op("add", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
		add.setHref(new Uri("add"));
		add.setOperationHandler(new OperationHandler() {
			@Override
			public Obj invoke(Obj in) {
				// Perform add logic
				resetExpiration();
				WatchOutImpl ret = new WatchOutImpl();
				
				if(in instanceof WatchIn) {
					WatchIn watchIn = (WatchIn) in;
					
					// If an attempt is made to add the same URI multiple times in the same WatchIn request,
					// then the server SHOULD only return the object once.
					// Therefore filter out duplicate URIs
					ArrayList<Uri> uris = new ArrayList<Uri>();
					for(IObj u : watchIn.get("hrefs").list()) {
						if (!uris.contains(u)) uris.add((Uri) u);
					}
					
					for(Uri uri : uris) {
						Obj o = broker.pullObj(uri);
						EventObserver<Obj> observer = null;
						
						if(!observedObjects.containsKey(uri.get())) {
							observedObjects.put(uri.get(), uri);
							
							if (o.isFeed()) {
								FeedFilter filter = ((Feed)o).getDefaultFilter();
								if (uri.size() > 0) filter = filter.getFilter(uri.list()[0]);
								observer = new FeedObserver(filter);
							} else {
								observer = new ObjObserver<Obj>();
							}
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
						
						if (o.isFeed()) {
							List<Obj> events = ((FeedObserver) observer).pollRefresh();
							for (Obj event : events) {
								obj.add(event);
							}
						}
						
						obj.setHref(uri);
						ret.values().add(obj, false);
					}
				}
				
				return ret;
			}			
		});
		return add;
	}

	public Op remove() {
		Op remove = new Op("remove", new Contract(WATCH_IN_CONTRACT), new Contract(OBIX_NIL));
		remove.setHref(new Uri("remove"));
		remove.setOperationHandler(new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				// Perform remove logic
				resetExpiration();
				if(in instanceof WatchIn){
					WatchIn watchIn = (WatchIn) in;
	
					for(IObj u : watchIn.get("hrefs").list()) {
						Uri uri = (Uri) u;

						Observer observer = observers.get(uri.getPath());

						observedObjects.remove(uri.getPath());

						observers.remove(uri.getPath());
						Obj o = broker.pullObj(uri);
						o.detach(observer);
					}					
				}

				return new NilImpl();
			}			
		});
		
		return remove;
	}

	public Op pollChanges() {
		Op pollChanges = new Op("pollChanges", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
		pollChanges.setHref(new Uri("pollChanges"));
		pollChanges.setOperationHandler(new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				resetExpiration();
				WatchOutImpl out = new WatchOutImpl();
				synchronized(observers) {
					// check for modified objects
					// NOTE pollChanges does not need to provide the events, only the latest state.
					
					for (String uri : observers.keySet()) {
						EventObserver<Obj> observer = observers.get(uri);
						List<Obj> events = observer.pollChanges();
						if(events.size() > 0) {
							// needs to be an obix object
							Obj obj = (Obj) observer.getSubject();
							Obj outItem = null;
							
							try {
								outItem = (Obj) obj.clone();
								outItem.setName(null, true);
								outItem.setHref(new Uri(uri));
								out.values().add(outItem, false);
							} catch (CloneNotSupportedException e) {
								log.info("Obj not clonable" + e.getMessage());
							}
							
							if (obj.isFeed()) {
								for (Obj event : events) {
									outItem.add(event);
								}
							}
						}
					}
				}
				
				return out;
			}		
		});
		
		return pollChanges;
	}

	public Op pollRefresh() {	
		Op pollRefresh = new Op("pollRefresh", new Contract(WATCH_IN_CONTRACT), new Contract(WATCH_OUT_CONTRACT));
		pollRefresh.setHref(new Uri("pollRefresh"));
		pollRefresh.setOperationHandler(new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				resetExpiration();
				WatchOutImpl out = new WatchOutImpl();
				// Perform refresh logic
				// Get a list of being-observed URI; get the corresponding object; notify the observer --> performing an update
				synchronized(observers){
					for (String uri : observers.keySet()) {
						EventObserver<Obj> observer = observers.get(uri);
						Obj beingObservedObject = (Obj) observer.getSubject();
						beingObservedObject.notifyObservers();
						
						Obj outItem = null;
						try {
							outItem = (Obj) beingObservedObject.clone();
							outItem.setName(null, true);
							outItem.setHref(new Uri(uri));
							out.values().add(outItem, false);
						} catch (CloneNotSupportedException e) {
							log.info("Obj not clonable" + e.getMessage());
						}
						
						if (beingObservedObject.isFeed()) {
							FeedObserver feedObserver = (FeedObserver) observer;
							List<Obj> events = feedObserver.pollRefresh();
							for (Obj event : events) {
								outItem.add(event);
							}
						}
						
						observer.pollChanges();
					}
				}
				return out;
			}		
		});
		
		return pollRefresh;
	}

	public Op delete() {
		Op delete = new Op("delete", new Contract(OBIX_NIL), new Contract(OBIX_NIL));
		delete.setHref(new Uri("delete"));
		delete.setOperationHandler(new OperationHandler(){
			@Override
			public Obj invoke(Obj in) {
				// Perform delete logic
				deleteWatch();

				return new NilImpl();
			}		
		});
		
		return delete;
	}
	
	private void deleteWatch() {
		for (Observer observer : observers.values()) {
			Obj beingObservedObject = (Obj) observer.getSubject();
			beingObservedObject.detach(observer);
			observers.remove(observer);
			observer = null;
		}
		
		expirationTimer.cancel(false);
		broker.removeObj(getFullContextPath());
	}
	
	/**
	 * Resets the lease timer
	 */
	private void resetExpiration() {
		if (expireTask == null) {
			expireTask = new Runnable() {
				public void run() {
					log.info("Lease for watch " + thisWatch().getHref().getPath() +  " expired");
					deleteWatch();
				}
			};
		}
		
		if (expirationTimer != null) {
			expirationTimer.cancel(false);
		}
		expirationTimer = timers.schedule(expireTask, lease.get(), TimeUnit.MILLISECONDS);
	}
	
	@Override
	public void refreshObject() {
		super.refreshObject();
		if (!expirationTimer.isCancelled())
			resetExpiration();
	}
	
	@Override
	public void writeObject(Obj input) {
		super.writeObject(input);
		
		if (input instanceof Reltime) {
			lease.set(((Reltime) input).get());
			resetExpiration();
		}
	}
}
