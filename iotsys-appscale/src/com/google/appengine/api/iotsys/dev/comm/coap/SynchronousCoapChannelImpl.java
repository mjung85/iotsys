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

package com.google.appengine.api.iotsys.dev.comm.coap;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import ch.ethz.inf.vs.californium.coap.CommunicatorFactory;
import ch.ethz.inf.vs.californium.coap.CommunicatorFactory.Communicator;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.MessageReceiver;
import ch.ethz.inf.vs.californium.coap.Request;

public class SynchronousCoapChannelImpl implements SynchronousCoapChannel {

	public static final long TIMEOUT_STD = 10000;
	
	private Communicator communicator;
	private BlockingQueue<Message> messageQueue;
	private long timeout = TIMEOUT_STD;
	
	public SynchronousCoapChannelImpl() throws IOException {
		CommunicatorFactory commFactory = CommunicatorFactory.getInstance();
		communicator = commFactory.getCommunicator();
		messageQueue = new LinkedBlockingQueue<Message>();
		communicator.registerReceiver(new CoapMessageReceiver(messageQueue));
	}
	
	@Override
	public long getTimeout() {
		return this.timeout;
	}
	
	@Override
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public Message sendRequest(Request request) throws IOException {
		try {
			communicator.sendMessage(request);
			Message response = messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
			if(response != null) {
				return response;
				}
		} catch(InterruptedException e) {			
		} 
		throw new IOException("could not contact server");
	}
	
	@Override
	public Message[] getRemainingMessages() {
		Message[] messages = new Message[messageQueue.size()];
		int i=0;
		while(!messageQueue.isEmpty()) {
			try {
				messages[i] = messageQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		return messages;
	}
	
	@Override
	public void clear() {
		this.messageQueue.clear();
	}
	
	private static class CoapMessageReceiver implements MessageReceiver {

		private BlockingQueue<Message> messageQueue;
		
		private CoapMessageReceiver(BlockingQueue<Message> synchBlockingQueue) {
			messageQueue = synchBlockingQueue;
		}
		
		@Override
		public void receiveMessage(Message msg) {
			try {
				messageQueue.put(msg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void close() {
		//TODO: close UDP socket
	}
	
}
