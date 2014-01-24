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

import com.google.appengine.api.iotsys.comm.Format;
import com.google.appengine.api.iotsys.comm.Protocol;
import com.google.appengine.api.iotsys.dev.ProtobufConverter;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.users.UserServiceFailureException;
import com.google.appengine.repackaged.com.google.protobuf.InvalidProtocolBufferException;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.IotsysServicePb;

/**
 * Calls the {@link LocalIotsysService} via the ApiProxy for standard GET, POST
 * and PUT requests to a gateway.
 * 
 * @author Clemens Puehringer
 * 
 */
public class IotsysConnectionProxy {

	private static final String PACKAGE = "iotsys";

	private static final String METHOD_NAME_GET_REQUEST = "sendGetRequest";
	private static final String METHOD_NAME_POST_REQUEST = "sendPostRequest";
	private static final String METHOD_NAME_PUT_REQUEST = "sendPutRequest";

	private static ThreadLocal<IotsysConnectionProxy> instance = new ThreadLocal<IotsysConnectionProxy>() {
		@Override
		public IotsysConnectionProxy initialValue() {
			return new IotsysConnectionProxy();
		}
	};

	public static IotsysConnectionProxy getInstance() {
		return instance.get();
	}

	private ProtobufConverter protoConverter;

	private IotsysConnectionProxy() {
		protoConverter = new ProtobufConverter();
	}

	public IotObject sendGetRequest(String host, int port, String uri,
			int format, int protocol) throws CommunicationException {
		IotObject response = makeSyncCall(METHOD_NAME_GET_REQUEST, host, port,
				uri, format, protocol, null);
		return response;
	}

	public IotObject sendPostRequest(String host, int port, String uri,
			int format, int protocol, IotObject request)
			throws CommunicationException {
		IotObject response = makeSyncCall(METHOD_NAME_POST_REQUEST, host, port,
				uri, format, protocol, request);
		return response;
	}

	public IotObject sendPutRequest(String host, int port, String uri,
			int format, int protocol, IotObject object)
			throws CommunicationException {
		IotObject response = makeSyncCall(METHOD_NAME_PUT_REQUEST, host, port,
				uri, format, protocol, object);
		return response;
	}

	private IotObject makeSyncCall(String methodName, String host, int port,
			String uri, int format, int protocol, IotObject request)
			throws CommunicationException {
		IotsysServicePb.IotsysRequestProto.Builder builder = IotsysServicePb.IotsysRequestProto
				.newBuilder();

		sanityCheck(host, port, uri, format, protocol);

		builder.setHost(host);
		builder.setPort(port);
		builder.setUri(uri);
		builder.setFormat(format);
		builder.setProtocol(protocol);

		if (request != null) {
			IotsysServicePb.IotObjectProto objectProto = protoConverter
					.iotObjectToProtobuf(request);
			builder.setObject(objectProto);
		}

		byte[] responseBytes;
		try {
			byte[] requestBytes = builder.build().toByteArray();
			responseBytes = ApiProxy.makeSyncCall(PACKAGE, methodName,
					requestBytes);
		} catch (ApiProxy.ApplicationException ex) {
			throw new UserServiceFailureException(ex.getErrorDetail());
		}
		IotsysServicePb.IotsysResponseProto responseProto;
		try {
			responseProto = IotsysServicePb.IotsysResponseProto
					.parseFrom(responseBytes);
			if (responseProto.getStatus().equals(
					IotsysServicePb.ResponseStatus.ERROR)) {
				String error = "unknown";
				if (responseProto.hasErrorMessage()) {
					error = responseProto.getErrorMessage();
				}
				throw new CommunicationException(error);
			}
		} catch (InvalidProtocolBufferException e) {
			throw new CommunicationException(e.getMessage());
		}
		return protoConverter.protobufToIotObject(responseProto.getObject());
	}

	private void sanityCheck(String host, int port, String uri, int format,
			int protocol) {
		if (host == null || host.length() <= 0) {
			throw new IllegalArgumentException("host must be a valid hostname");
		}
		if (port <= 0) {
			throw new IllegalArgumentException(
					"port must be a valid port number [1-65535]");
		}
		if (uri == null || uri.length() <= 0) {
			throw new IllegalArgumentException(
					"uri must be a valid uri to a resource");
		}
		if (!Format.isValid(format)) {
			throw new IllegalArgumentException(
					"format must be a valid format constant specified in Format");
		}
		if (!Protocol.isValid(protocol)) {
			throw new IllegalArgumentException(
					"protocol must be a valid protocol constant specified in Protocol");
		}
	}

}
