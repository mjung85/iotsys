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

package com.google.appengine.api.iotsys;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.comm.Format;
import com.google.appengine.api.iotsys.comm.Protocol;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.object.IotError;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotOperation;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.api.iotsys.object.watch.IotWatch;

public class IotsysServiceImpl implements IotsysService {

	private static final Logger logger = Logger
			.getLogger(IotsysServiceImpl.class.getName());

	private static final String WATCH_SERVICE_URI = "/watchService/";

	private String host;
	private int port;

	private int format = Format.OBIX_PLAINTEXT;
	private int protocol = Protocol.COAP;

	public IotsysServiceImpl(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public IotsysServiceImpl(String host, int port, int format, int protocol) {
		this(host, port);
		if(!Format.isValid(format)) {
			throw new IllegalArgumentException("format must be a constant value declared in Format");
		}
		if(!Protocol.isValid(protocol)) {
			throw new IllegalArgumentException("protocol must be a constant value declared in Protocol");
		}
		this.format = format;
		this.protocol = protocol;
	}

	@Override
	public IotObject retrieveObject(String href) throws CommunicationException {
		if (!href.endsWith("/")) {
			href = href + "/";
		}

		IotObject object = IotsysConnectionProxy.getInstance().sendGetRequest(
				host, port, href, format, protocol);

		return object;
	}

	@Override
	public IotWatch createWatch() throws CommunicationException {
		return createWatch(WATCH_SERVICE_URI);
	}

	@Override
	public IotWatch createWatch(String watchServiceUri)
			throws CommunicationException {
		IotOperation operation = new IotOperation();
		operation.setHost(host);
		operation.setPort(port);
		operation.setFormat(format);
		operation.setProtocol(protocol);
		if (!watchServiceUri.endsWith("/")) {
			watchServiceUri = watchServiceUri + "/";
		}
		operation.setHref(watchServiceUri + "make");
		IotObject response = operation.invoke(null);

		if (!(response instanceof IotWatch)) {
			if (response instanceof IotError) {
				IotError error = (IotError) response;
				throw new CommunicationException("could not create watch: "
						+ error.getDisplay());
			}
			throw new CommunicationException("received "
					+ response.getClass().getSimpleName()
					+ " instead of IotWatch");
		}
		return (IotWatch) response;
	}

	@Override
	public void recordObject(IotObject object, long timeout)
			throws CommunicationException, HistoryExistsException {
		object.setFormat(format);
		object.setProtocol(protocol);
		IotsysHistoryProxy.getInstance().recordObject(object, timeout);
	}

	@Override
	public void recordWatch(IotObject watch) throws CommunicationException,
			HistoryExistsException {
		watch.setFormat(format);
		watch.setProtocol(protocol);
		IotsysHistoryProxy.getInstance().recordWatch(watch);
	}

	@Override
	public void recordHistory(IotObject history) throws CommunicationException,
			HistoryExistsException {
		history.setFormat(format);
		history.setProtocol(protocol);
		IotsysHistoryProxy.getInstance().recordHistory(history);
	}

	@Override
	public List<IotHistoryRecord> getHistoryData(IotObject object)
			throws CommunicationException, NoSuchHistoryException {
		return IotsysHistoryProxy.getInstance().getHistoryData(object);
	}

	@Override
	public boolean hasActiveHistoryRecording(IotObject object)
			throws CommunicationException {
		return IotsysHistoryProxy.getInstance().hasHistoryRecording(object);
	}

	@Override
	public boolean hasHistoryData(IotObject object)
			throws CommunicationException {
		return IotsysHistoryProxy.getInstance().hasHistoryData(object);
	}

	@Override
	public void stopHistoryRecording(IotObject object)
			throws CommunicationException, NoSuchHistoryException {
		IotsysHistoryProxy.getInstance().stopHistoryRecording(object);
	}

	@Override
	public void deleteHistoryRecords(IotObject object)
			throws CommunicationException {
		IotsysHistoryProxy.getInstance().deleteRecords(object);
	}

}
