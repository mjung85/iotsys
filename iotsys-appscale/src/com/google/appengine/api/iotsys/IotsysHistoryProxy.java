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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.dev.ProtobufConverter;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.object.IotBoolean;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.api.users.UserServiceFailureException;
import com.google.appengine.repackaged.com.google.protobuf.InvalidProtocolBufferException;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.IotsysServicePb;
import com.google.apphosting.api.IotsysServicePb.IotObjectProto;

/**
 * Calls the {@link LocalIotsysService} via the ApiProxy for history related
 * operations
 * 
 * @author Clemens Puehringer
 * 
 */
public class IotsysHistoryProxy {

	private static final Logger logger = Logger
			.getLogger(IotsysHistoryProxy.class.getName());

	private static final String PACKAGE = "iotsys";

	private static final String METHOD_NAME_RECORD_HISTORY = "recordHistory";

	private static final String METHOD_NAME_STOP_HISTORY = "stopHistory";
	private static final String METHOD_NAME_HAS_HISTORY_REC = "hasHistoryRecording";
	private static final String METHOD_NAME_HAS_HISTORY_DATA = "hasHistoryData";
	private static final String METHOD_NAME_GET_HISTORY = "getHistory";
	private static final String METHOD_NAME_DELETE_HISTORY = "deleteHistory";

	private static ThreadLocal<IotsysHistoryProxy> instance = new ThreadLocal<IotsysHistoryProxy>() {
		@Override
		public IotsysHistoryProxy initialValue() {
			return new IotsysHistoryProxy();
		}
	};

	/**
	 * get a thread-local instance of the history proxy
	 * 
	 * @return a thread-local instance of the history proxy
	 */
	public static IotsysHistoryProxy getInstance() {
		return instance.get();
	}

	private ProtobufConverter protoConverter;

	private IotsysHistoryProxy() {
		protoConverter = new ProtobufConverter();
	}

	/**
	 * poll the given object's state regularly with an interval of [timeout]
	 * milliseconds.
	 * 
	 * @see IotsysService#recordObject(IotObject, long) for more information on
	 *      the timeout
	 * 
	 * @param object
	 *            the object to poll regularly
	 * @param timeout
	 *            the timeout before polling the object again
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 * @throws HistoryExistsException
	 *             if something goes wrong while executing the request
	 */
	public void recordObject(IotObject object, long timeout)
			throws CommunicationException, HistoryExistsException {
		makeRecordCall(IotsysServicePb.IotHistoryType.HISTORY_POLL, object,
				timeout);
	}

	/**
	 * record changes of the given watch, the object itself need not be of type
	 * IotWatch, but the href of the given object must point to a valid
	 * obix:Watch object on the gateway
	 * 
	 * @param watch
	 *            an object with valid host, port and href pointing to an
	 *            obix:Watch on the gateway
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 * @throws HistoryExistsException
	 *             if something goes wrong while executing the request
	 */
	public void recordWatch(IotObject watch) throws CommunicationException,
			HistoryExistsException {
		makeRecordCall(IotsysServicePb.IotHistoryType.HISTORY_WATCH, watch, -1);
	}

	/**
	 * record changes in the given history, the object itself need not be of
	 * type IotHistory, but the href of the given object must point to a valid
	 * obix:History object on the gateway
	 * 
	 * @param history
	 *            an object with valid host, port and href pointing to an
	 *            obix:History on the gateway
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 * @throws HistoryExistsException
	 *             if there is already an active worker recording the state of
	 *             this history
	 */
	public void recordHistory(IotObject history) throws CommunicationException,
			HistoryExistsException {
		makeRecordCall(IotsysServicePb.IotHistoryType.HISTORY_FEED, history, -1);
	}

	/**
	 * stops the recording of history data for the given object, existent
	 * records will not be deleted and can still be queried
	 * 
	 * @param object
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 * @throws NoSuchHistoryException
	 *             if there is no active worker to record the given objects
	 *             state
	 */
	public void stopHistoryRecording(IotObject object)
			throws CommunicationException, NoSuchHistoryException {
		try {
			makeVoidCall(METHOD_NAME_STOP_HISTORY, object);
		} catch (HistoryExistsException e) {
			e.printStackTrace();
		}
	}

	/**
	 * checks if there is a worker active to record the given objects state,
	 * only relevant fields have to be set in the given object (host, port,
	 * href)
	 * 
	 * @param object
	 * @return true if there is an active worker to record the objects state,
	 *         false otherwise
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 */
	public boolean hasHistoryRecording(IotObject object)
			throws CommunicationException {
		return makeBooleanCall(METHOD_NAME_HAS_HISTORY_REC, object);
	}

	/**
	 * check if there are history records for the given object, only relevant
	 * fields have to be set in the given object (host, port, href)
	 * 
	 * @param object
	 * @return
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 */
	public boolean hasHistoryData(IotObject object)
			throws CommunicationException {
		return makeBooleanCall(METHOD_NAME_HAS_HISTORY_DATA, object);
	}

	/**
	 * get all history records for the given object, only relevant fields have
	 * to be set in the given object (host, port, href)
	 * 
	 * @param object
	 *            the object for which to get the records
	 * @return all available history records for the given object
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 * @throws NoSuchHistoryException
	 *             if no records are found for the given object
	 */
	public List<IotHistoryRecord> getHistoryData(IotObject object)
			throws CommunicationException, NoSuchHistoryException {
		List<IotObject> response = null;
		response = makeDataCall(METHOD_NAME_GET_HISTORY, object);

		List<IotHistoryRecord> records = new ArrayList<IotHistoryRecord>();
		for (IotObject o : response) {
			if (o instanceof IotHistoryRecord) {
				records.add((IotHistoryRecord) o);
			}
		}
		return records;
	}

	/**
	 * delete all history records for the given object, only relevant fields
	 * have to be set in the given object (host, port, href)
	 * 
	 * @param object
	 *            the object to delete the history records for
	 * @throws CommunicationException
	 *             if something goes wrong while executing the request
	 */
	public void deleteRecords(IotObject object) throws CommunicationException {
		try {
			makeVoidCall(METHOD_NAME_DELETE_HISTORY, object);
		} catch (NoSuchHistoryException e) {
			e.printStackTrace();
		} catch (HistoryExistsException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * checks that the important fields of the given object (host, port, href)
	 * are not null and contain accepted values
	 * 
	 * @param object
	 *            the object to check
	 */
	private void sanityCheck(IotObject object) {
		if (object == null) {
			throw new IllegalArgumentException("object must not be null");
		}
		if (object.getHost() == null || object.getHost().length() <= 0) {
			throw new IllegalArgumentException("host field must be set");
		}
		if (object.getPort() <= 0) {
			throw new IllegalArgumentException(
					"port field must be a valid port number [1-65535]");
		}
		if (object.getHref() == null || object.getHref().length() <= 0) {
			throw new IllegalArgumentException("href field must be set");
		}
	}

	/**
	 * create a new {@link IotsysServicePb.IotsysHistoryRequestProto.Builder}
	 * and initialize it with basic data from the object, this includes the
	 * host, port and href of the object
	 * 
	 * @param object
	 *            the object with which to initialize the builder
	 * @return a builder with its host, port and uri fields set
	 */
	private IotsysServicePb.IotsysHistoryRequestProto.Builder initializeBuilder(
			IotObject object) {
		IotsysServicePb.IotsysHistoryRequestProto.Builder builder = IotsysServicePb.IotsysHistoryRequestProto
				.newBuilder();
		builder.setHost(object.getHost());
		builder.setPort(object.getPort());
		builder.setUri(object.getHref());
		return builder;
	}

	/**
	 * makes a call to the {@link IotsysHistoryManager} via
	 * {@link LocalIotsysService} to record a history for the given object with
	 * the given timeout
	 * 
	 * @param type
	 *            the type of recording (poll, watch, history)
	 * @param object
	 *            the object for which to record the history
	 * @param timeout
	 *            the timeout for the polling of the object status
	 * @throws CommunicationException
	 *             if something goes wrong on while executing the request
	 * @throws HistoryExistsException
	 *             if there is already a recording taking place
	 */
	private void makeRecordCall(IotsysServicePb.IotHistoryType type,
			IotObject object, long timeout) throws CommunicationException,
			HistoryExistsException {
		sanityCheck(object);
		IotsysServicePb.IotsysHistoryRequestProto.Builder builder = initializeBuilder(object);
		builder.setRecordingType(type);
		builder.setFormat(object.getFormat());
		builder.setProtocol(object.getProtocol());
		builder.setTimeout(timeout);

		IotsysServicePb.IotsysHistoryResponseProto responseProto = doMakeCall(
				METHOD_NAME_RECORD_HISTORY, builder.build());
		checkForHistoryExistsException(responseProto);
	}

	/**
	 * makes a call to the {@link IotsysHistoryManager} via
	 * {@link LocalIotsysService} where a boolean return value is expected
	 * 
	 * @param methodName
	 *            the actual name of the method to call in
	 *            {@link LocalIotsysService}
	 * @param object
	 *            an object which contains data relevant to the
	 *            {@link IotsysHistoryManager}, such as host, port and href
	 * @return the returned boolean value from {@link LocalIotsysService}
	 * @throws CommunicationException
	 *             if something goes wrong on while executing the request
	 */
	private boolean makeBooleanCall(String methodName, IotObject object)
			throws CommunicationException {
		sanityCheck(object);
		IotsysServicePb.IotsysHistoryRequestProto.Builder builder = initializeBuilder(object);
		IotsysServicePb.IotsysHistoryResponseProto responseProto = doMakeCall(
				methodName, builder.build());
		if (responseProto.getDataCount() <= 0) {
			throw new CommunicationException(
					"expected boolean datapoint as result, but HistoryManager returned no result");
		}
		IotObject responseObject = protoConverter
				.protobufToIotObject(responseProto.getData(0));
		if (!(responseObject instanceof IotBoolean)) {
			throw new CommunicationException(
					"expected boolean datapoint as result, but proxy returned "
							+ responseObject.getClass().getSimpleName());
		}
		return ((IotBoolean) responseObject).getValue();
	}

	/**
	 * makes a call to the {@link IotsysHistoryManager} via
	 * {@link LocalIotsysService} where no return value is expected
	 * 
	 * @param methodName
	 *            the actual name of the method to call in
	 *            {@link LocalIotsysService}
	 * @param object
	 *            an object which contains data relevant to the
	 *            {@link IotsysHistoryManager}, such as host, port and href
	 * @throws CommunicationException
	 *             if something goes wrong on while executing the request
	 * @throws NoSuchHistoryException
	 *             if no recording exists for the given object
	 * @throws HistoryExistsException
	 *             if there is already a recording taking place
	 */
	private void makeVoidCall(String methodName, IotObject object)
			throws CommunicationException, NoSuchHistoryException,
			HistoryExistsException {
		sanityCheck(object);
		IotsysServicePb.IotsysHistoryRequestProto.Builder builder = initializeBuilder(object);
		IotsysServicePb.IotsysHistoryResponseProto responseProto = doMakeCall(
				methodName, builder.build());
		checkForHistoryExistsException(responseProto);
		checkForNoSuchHistoryException(responseProto);
	}

	/**
	 * retrieve available history data from the {@link IotsysHistoryManager} via
	 * {@link LocalIotsysService}
	 * 
	 * @param methodName
	 *            the actual name of the method to call in
	 *            {@link LocalIotsysService}
	 * @param object
	 *            the object for which to get the data
	 * @return a list containing recorded history data for the given object
	 * @throws CommunicationException
	 *             if something goes wrong on while executing the request
	 * @throws NoSuchHistoryException
	 *             if there are no recordings for that object
	 */
	private List<IotObject> makeDataCall(String methodName, IotObject object)
			throws CommunicationException, NoSuchHistoryException {
		sanityCheck(object);
		IotsysServicePb.IotsysHistoryRequestProto.Builder builder = initializeBuilder(object);
		IotsysServicePb.IotsysHistoryResponseProto responseProto = doMakeCall(
				methodName, builder.build());

		checkForNoSuchHistoryException(responseProto);

		List<IotObject> responseObjects = new ArrayList<IotObject>();
		for (IotObjectProto prot : responseProto.getDataList()) {
			responseObjects.add(protoConverter.protobufToIotObject(prot));
		}
		return responseObjects;

	}

	/**
	 * invokes the given method of {@link LocalIotsysService} via the ApiProxy
	 * so that it will be given higher security privileges
	 * 
	 * @param methodName
	 *            The method to invoke in {@link LocalIotsysService}
	 * @param request
	 *            The Protocol Buffer input argument of that method
	 * @return the return value of that method
	 * @throws CommunicationException
	 *             if something goes wrong on while executing the request
	 */
	private IotsysServicePb.IotsysHistoryResponseProto doMakeCall(
			String methodName, IotsysServicePb.IotsysHistoryRequestProto request)
			throws CommunicationException {
		byte[] responseBytes;
		try {
			byte[] requestBytes = request.toByteArray();
			responseBytes = ApiProxy.makeSyncCall(PACKAGE, methodName,
					requestBytes);
		} catch (ApiProxy.ApplicationException ex) {
			throw new UserServiceFailureException(ex.getErrorDetail());
		}
		IotsysServicePb.IotsysHistoryResponseProto responseProto;
		try {
			responseProto = IotsysServicePb.IotsysHistoryResponseProto
					.parseFrom(responseBytes);
			checkForCommunicationException(responseProto);
		} catch (InvalidProtocolBufferException e) {
			throw new CommunicationException(e.getMessage());
		}
		return responseProto;
	}

	/**
	 * Checks if the response contains an error code for a
	 * {@link CommunicationException} and if so, throws that exception
	 * 
	 * @param responseProto
	 *            the response to check
	 * @throws CommunicationException
	 *             if the response contains an error code for a
	 *             {@link CommunicationException}, the message of the thrown
	 *             exception will match the response's error message
	 */
	public void checkForCommunicationException(
			IotsysServicePb.IotsysHistoryResponseProto responseProto)
			throws CommunicationException {
		if (responseProto.getStatus().equals(
				IotsysServicePb.ResponseStatus.ERROR)) {
			String error = "unknown";
			if (responseProto.hasErrorMessage()) {
				error = responseProto.getErrorMessage();
			}
			throw new CommunicationException(error);
		}
	}

	/**
	 * Checks if the response contains an error code for a
	 * {@link HistoryExistsException} and if so, throws that exception
	 * 
	 * @param responseProto
	 *            the response to check
	 * @throws HistoryExistsException
	 *             if the response contains an error code for a
	 *             {@link HistoryExistsException}, the message of the thrown
	 *             exception will match the response's error message
	 */
	public void checkForHistoryExistsException(
			IotsysServicePb.IotsysHistoryResponseProto responseProto)
			throws HistoryExistsException {
		if (responseProto.getStatus().equals(
				IotsysServicePb.ResponseStatus.ERROR_HISTORY_EXISTS)) {
			String error = "unknown";
			if (responseProto.hasErrorMessage()) {
				error = responseProto.getErrorMessage();
			}
			throw new HistoryExistsException(error);
		}
	}

	/**
	 * Checks if the response contains an error code for a
	 * {@link NoSuchHistoryException} and if so, throws that exception
	 * 
	 * @param responseProto
	 *            the response to check
	 * @throws NoSuchHistoryException
	 *             if the response contains an error code for a
	 *             {@link NoSuchHistoryException}, the message of the thrown
	 *             exception will match the response's error message
	 */
	public void checkForNoSuchHistoryException(
			IotsysServicePb.IotsysHistoryResponseProto responseProto)
			throws NoSuchHistoryException {
		if (responseProto.getStatus().equals(
				IotsysServicePb.ResponseStatus.ERROR_NO_SUCH_HISTORY)) {
			String error = "unknown";
			if (responseProto.hasErrorMessage()) {
				error = responseProto.getErrorMessage();
			}
			throw new NoSuchHistoryException(error);
		}
	}

}
