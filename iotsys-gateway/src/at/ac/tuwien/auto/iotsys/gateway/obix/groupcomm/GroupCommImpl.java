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

package at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.layers.MulticastUDPLayer;

import at.ac.tuwien.auto.iotsys.commons.OperationHandler;
import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.observer.Observer;
import at.ac.tuwien.auto.iotsys.gateway.obix.observer.Subject;
import at.ac.tuwien.auto.iotsys.gateway.service.GroupCommService;
import at.ac.tuwien.auto.iotsys.gateway.service.impl.GroupCommServiceImpl;
import obix.Contract;
import obix.List;
import obix.Obj;
import obix.Op;
import obix.Ref;
import obix.Str;
import obix.Uri;

public class GroupCommImpl extends Obj implements GroupComm, Observer{
	
	private static final Logger log = Logger.getLogger(GroupCommImpl.class.getName());
	
	protected List groups = new List();
	
	protected Op joinGroup = new Op();
	protected Op leaveGroup = new Op();
	
	protected Obj datapoint;
	protected GroupCommService groupCommService;
	
	public GroupCommImpl(Obj datapoint, GroupCommService groupCommService){
		
		this.groupCommService = groupCommService;
		
		this.setName("groupComm");
		this.setHref(new Uri("groupComm"));
		this.setIs(new Contract(GroupComm.CONTRACT));
		
		groups.setName(GroupComm.GROUPS_NAME);
		groups.setHref(new Uri(GroupComm.GROUPS_HREF));
		groups.setOf(new Contract("obix:str"));
		
		joinGroup.setName("joinGroup");
		joinGroup.setHref(new Uri("joinGroup"));
		joinGroup.setIn(new Contract("obix:str"));
		joinGroup.setOut(new Contract("obix:list"));
				
		leaveGroup.setName("leaveGroup");
		leaveGroup.setHref(new Uri("leaveGroup"));
		leaveGroup.setIn(new Contract("obix:str"));
		leaveGroup.setOut(new Contract("obix:list"));
		
		this.datapoint = datapoint;		
		this.datapoint.attach(this);
		
		this.add(groups);
		this.add(joinGroup);
		this.add(leaveGroup);
	}
	
	@Override
	public void initialize(){
		this.setHref(new Uri(this.datapoint.getFullContextPath()
				+ "/groupComm"));
		ObjectBrokerImpl.getInstance().addObj(this, false);
		
		String queryHref = datapoint.getFullContextPath()
				+ "/groupComm/joinGroup";

		ObjectBrokerImpl.getInstance().addOperationHandler(

		new Uri(queryHref), new OperationHandler() {
			public Obj invoke(Obj in) {
				return GroupCommImpl.this.joinGroup(in);
			}
		});

		String rollupHref = datapoint.getFullContextPath()
				+ "/groupComm/leaveGroup";

		ObjectBrokerImpl.getInstance().addOperationHandler(new Uri(rollupHref),
				new OperationHandler() {
					public Obj invoke(Obj in) {
						return GroupCommImpl.this.leaveGroup(in);
					}
				});

		// add history reference in the parent element
		if (datapoint.getParent() != null) {
			Ref ref = new Ref(datapoint.getName() + " groupComm", new Uri(
					datapoint.getHref() + "/groupComm"));
			ref.setIs(new Contract(GroupComm.CONTRACT));
			datapoint.getParent().add(ref);
		}
	}
	

	@Override
	public Op joinGroup() {
		return joinGroup;
	}

	@Override
	public Op leaveGroup() {
		return leaveGroup;
	}

	@Override
	public synchronized List groups() {
		return groups;
	}
	
	public synchronized Obj joinGroup(Obj in){
		log.finest("Joining group comm endpoint.");
		if(in instanceof Str){
			Str str = (Str) in;
			groups.add(str);
			try {
				Inet6Address groupAddr = (Inet6Address) Inet6Address.getByName(str.get());
				GroupCommServiceImpl.getInstance().registerObject(groupAddr, this.datapoint);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return groups;	
	}
	
	public synchronized Obj leaveGroup(Obj in){
		log.finest("Leaving group comm endpoint.");
		if(in instanceof Str){
			Str str = (Str) in;
			Obj[] list = groups.list();
			for(Obj obj : list){
				if(obj instanceof Str){
					Str strObj = (Str) obj;
					if(strObj.get().equals(str.get())){
						groups.remove(strObj);
					}
					
					try {
						Inet6Address groupAddr = (Inet6Address) Inet6Address.getByName(str.get());
						GroupCommServiceImpl.getInstance().unregisterObject(groupAddr, this.datapoint);
						
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}	
		}
		return groups;	
	}

	@Override
	public synchronized void update(Object state) {
		log.finest("Object updated: " + state);	
		log.finest("Request type: " + MulticastUDPLayer.getRequestType());
		log.finest("Group addr: " + MulticastUDPLayer.getMulticastAddress());
		
		Obj[] list = groups.list();
		for(Obj obj : list){
			if(obj instanceof Str){
				Str strObj = (Str) obj;
				Inet6Address group;
				try {
					group = (Inet6Address) Inet6Address.getByName(strObj.get());
					if(!group.equals(MulticastUDPLayer.getMulticastAddress())){
						log.finest("Sending out update of " + datapoint.getFullContextPath() + " to group " + group);
						GroupCommServiceImpl.getInstance().sendUpdate(group, state);
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
				
	}

	@Override
	public void setSubject(Subject object) {
				
	}

	@Override
	public Subject getSubject() {
		
		return null;
	}
}
