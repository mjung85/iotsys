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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot;

import java.util.Hashtable;

import obix.Obj;
import obix.Op;
import obix.Ref;
import obix.Uri;
import obix.contracts.Lobby;

public class LobbyImpl extends Obj implements Lobby {
	private final Hashtable<String, Ref> references = new Hashtable<String, Ref>();
	private Ref about = new Ref();
	
	public LobbyImpl(){
		this.setHref(new Uri("http://localhost/obix"));
		about.setName("about");
		about.setHref(new Uri("about"));
		this.add(about);
	}

	@Override
	public Ref about() {
		return about;
	}

	@Override
	public Op batch() {
		return null;
	}

	@Override
	public Ref watchService() {
		return null;
	}
	
	public void addReference(String href, Ref ref){
		synchronized(references){
			references.put(href, ref);
			
		}
		this.add(ref);
	}
	
	public void removeReference(String href){
		synchronized(references){
			Ref ref = references.get(href);
			if(ref != null)
				this.remove(ref);
			references.remove(href);			
		}
	}
}
