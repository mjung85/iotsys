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

package com.google.appengine.api.iotsys.dev;

import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.dev.comm.FormatRegistry;
import com.google.appengine.api.iotsys.dev.comm.ProtocolRegistry;
import com.google.appengine.api.iotsys.dev.comm.TransportProtocol;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.object.IotBoolean;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.spi.ServiceProvider;
import com.google.appengine.tools.development.AbstractLocalRpcService;
import com.google.appengine.tools.development.LocalRpcService;
import com.google.apphosting.api.IotsysServicePb;
import com.google.apphosting.api.IotsysServicePb.ResponseStatus;

@ServiceProvider(LocalRpcService.class)
public class LocalIotsysService extends AbstractLocalRpcService {

	private static final Logger logger = Logger.getLogger(LocalIotsysService.class.getName());
	
	public static final String PACKAGE = "iotsys";

	private static final int REQUEST_TYPE_GET = 0;
	private static final int REQUEST_TYPE_PUT = 1;
	private static final int REQUEST_TYPE_POST = 2;
	
	private ProtobufConverter converter;

	public LocalIotsysService() {
		converter = new ProtobufConverter();
	}

	@Override
	public String getPackage() {
		return PACKAGE;
	}

	public IotsysServicePb.IotsysResponseProto sendGetRequest(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysRequestProto request) {
		return doSendRequest(LocalIotsysService.REQUEST_TYPE_GET, request);
	}

	public IotsysServicePb.IotsysResponseProto sendPostRequest(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysRequestProto request) {
		return doSendRequest(LocalIotsysService.REQUEST_TYPE_POST, request);
	}

	public IotsysServicePb.IotsysResponseProto sendPutRequest(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysRequestProto request) {
		return doSendRequest(LocalIotsysService.REQUEST_TYPE_PUT, request);
	}

	private IotsysServicePb.IotsysResponseProto doSendRequest(int type,
			IotsysServicePb.IotsysRequestProto request) {
		IotsysServicePb.IotsysResponseProto.Builder responseBuilder = IotsysServicePb.IotsysResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}
		TransportProtocol protocol = chooseProtocol(request, responseBuilder);
		if (protocol == null) {
			return responseBuilder.build();
		}
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		IotObject requestObject = null;
		if(request.hasObject()) {
			requestObject = converter.protobufToIotObject(request.getObject());
		}			

		try {
			IotObject responseObject = null;
			if(type == LocalIotsysService.REQUEST_TYPE_PUT) {
				responseObject = protocol.sendPutRequest(host, port, uri,
						requestObject);
			} else if(type == LocalIotsysService.REQUEST_TYPE_POST) {
				responseObject = protocol.sendPostRequest(host, port, uri,
						requestObject);
			} else {
				responseObject = protocol.sendGetRequest(host, port, uri);
			}
			responseObject.setFormat(request.getFormat());
			responseObject.setProtocol(request.getFormat());

			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
			responseBuilder.setObject(converter
					.iotObjectToProtobuf(responseObject));
		} catch (Exception e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(e.getMessage());
		}

		return responseBuilder.build();
	}
	
	private TransportProtocol chooseProtocol(
			IotsysServicePb.IotsysRequestProto request,
			IotsysServicePb.IotsysResponseProto.Builder responseBuilder) {
		TransportProtocol protocol = null;
		try {
			protocol = ProtocolRegistry.getProtocol(request.getProtocol());
			protocol.setTransportFormat(FormatRegistry.getFormat(request.getFormat())); 
		} catch (Exception e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			if(e.getMessage() != null) {
				responseBuilder.setErrorMessage(e.getMessage());
			} else {
				responseBuilder.setErrorMessage(e.getClass().getSimpleName());
			}
		}
		return protocol;
	}

	public IotsysServicePb.IotsysHistoryResponseProto recordHistory(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {

		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		IotsysServicePb.IotHistoryType type = request.getRecordingType();
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		int format = request.getFormat();
		int protocol = request.getProtocol();
		try {
			if (type == IotsysServicePb.IotHistoryType.HISTORY_FEED) {
				IotsysHistoryManager.startFeedRecording(host, port, uri,
						format, protocol);
			} else if (type == IotsysServicePb.IotHistoryType.HISTORY_WATCH) {
				IotsysHistoryManager.startWatchRecording(host, port, uri,
						format, protocol);
			} else {
				long timeout = -1;
				if (request.hasTimeout()) {
					timeout = request.getTimeout();
				}
				IotsysHistoryManager.startPollRecording(host, port, uri,
						format, protocol, timeout);
			}
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		} catch (CommunicationException e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(e.getMessage());
		} catch (HistoryExistsException e) {
			responseBuilder
					.setStatus(IotsysServicePb.ResponseStatus.ERROR_HISTORY_EXISTS);
			responseBuilder.setErrorMessage(e.getMessage());
		}
		return responseBuilder.build();
	}

	public IotsysServicePb.IotsysHistoryResponseProto stopHistory(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {

		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		try {
			IotsysHistoryManager.stopRecording(host, port, uri);
		} catch (NoSuchHistoryException e) {
			responseBuilder
					.setStatus(IotsysServicePb.ResponseStatus.ERROR_NO_SUCH_HISTORY);
			responseBuilder.setErrorMessage(e.getMessage());
		}
		return responseBuilder.build();
	}

	public IotsysServicePb.IotsysHistoryResponseProto hasHistoryRecording(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {
		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		IotBoolean ret = new IotBoolean();
		ret.setValue(IotsysHistoryManager.hasRecording(host, port, uri));
		responseBuilder.addData(converter.iotObjectToProtobuf(ret));
		return responseBuilder.build();
	}

	public IotsysServicePb.IotsysHistoryResponseProto hasHistoryData(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {
		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		IotBoolean ret = new IotBoolean();
		try {
			ret.setValue(IotsysHistoryManager.hasData(host, port, uri));
		} catch (CommunicationException e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(e.getMessage());
		}
		responseBuilder.addData(converter.iotObjectToProtobuf(ret));
		return responseBuilder.build();
	}

	public IotsysServicePb.IotsysHistoryResponseProto getHistory(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {
		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		try {
			List<IotHistoryRecord> historyRecords = IotsysHistoryManager
					.getData(host, port, uri);
			for (IotHistoryRecord o : historyRecords) {
				responseBuilder.addData(converter.iotObjectToProtobuf(o));
			}
		} catch (CommunicationException e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(e.getMessage());
		} catch (NoSuchHistoryException e) {
			responseBuilder
					.setStatus(IotsysServicePb.ResponseStatus.ERROR_NO_SUCH_HISTORY);
			responseBuilder.setErrorMessage(e.getMessage());
		}

		return responseBuilder.build();
	}

	public IotsysServicePb.IotsysHistoryResponseProto deleteHistory(
			LocalRpcService.Status status,
			IotsysServicePb.IotsysHistoryRequestProto request) {
		IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder = IotsysServicePb.IotsysHistoryResponseProto
				.newBuilder();

		if (!sanityCheck(request, responseBuilder)) {
			return responseBuilder.build();
		}

		responseBuilder.setStatus(IotsysServicePb.ResponseStatus.OK);
		String host = request.getHost();
		int port = request.getPort();
		String uri = request.getUri();
		try {
			IotsysHistoryManager.deleteData(host, port, uri);
		} catch (CommunicationException e) {
			responseBuilder.setStatus(IotsysServicePb.ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(e.getMessage());
		}

		return responseBuilder.build();
	}

	private boolean sanityCheck(
			IotsysServicePb.IotsysHistoryRequestProto request,
			IotsysServicePb.IotsysHistoryResponseProto.Builder responseBuilder) {
		String message = null;
		if (!(request.hasHost()) || request.getHost().length() <= 0) {
			message = "must specify a valid host for history operations";
		} else if (!(request.hasPort()) || request.getHost().length() <= 0) {
			message = "must specify a valid port for history operations";
		} else if (!(request.hasUri()) || request.getUri().length() <= 0) {
			message = "must specify a valid uri for history operations";
		}
		if (message != null) {
			responseBuilder.setStatus(ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(message);
			return false;
		}
		return true;
	}

	private boolean sanityCheck(IotsysServicePb.IotsysRequestProto request,
			IotsysServicePb.IotsysResponseProto.Builder responseBuilder) {
		String message = null;
		if (!(request.hasHost()) || request.getHost().length() <= 0) {
			message = "must specify a valid host";
		} else if (!(request.hasPort()) || request.getHost().length() <= 0) {
			message = "must specify a valid port";
		} else if (!(request.hasUri()) || request.getUri().length() <= 0) {
			message = "must specify a valid uri";
		}
		if (message != null) {
			responseBuilder.setStatus(ResponseStatus.ERROR);
			responseBuilder.setErrorMessage(message);
			return false;
		}
		return true;
	}

}