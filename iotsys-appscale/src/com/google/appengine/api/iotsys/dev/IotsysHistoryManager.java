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

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.google.appengine.api.iotsys.dev.comm.FormatRegistry;
import com.google.appengine.api.iotsys.dev.comm.ProtocolRegistry;
import com.google.appengine.api.iotsys.dev.comm.TransportProtocol;
import com.google.appengine.api.iotsys.dev.persistence.HashMapPersistenceManager;
import com.google.appengine.api.iotsys.dev.persistence.IotsysPersistenceManager;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.exception.ObjectObsoleteException;
import com.google.appengine.api.iotsys.object.IotAbsoluteTime;
import com.google.appengine.api.iotsys.object.IotError;
import com.google.appengine.api.iotsys.object.IotFeed;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.IotRelativeTime;
import com.google.appengine.api.iotsys.object.history.IotHistory;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.api.iotsys.object.watch.IotWatch;
import com.google.appengine.api.iotsys.object.watch.IotWatchOut;

public class IotsysHistoryManager {

	private static final Logger logger = Logger
			.getLogger(IotsysHistoryManager.class.getName());

	private static final String WATCH_SERVICE_URI = "/watchService/make";

	private static final long TIMEOUT_COMM_ERROR = 60000;
	private static final long TIMEOUT_DEFAULT_POLL = 30000;
	private static final long TIMEOUT_MIN_POLL = 10000;
	private static final long TIMEOUT_DEFAULT_WATCH = 30000;

	private static Map<String, HistoryWorker> activeWorkers = new HashMap<String, HistoryWorker>();

	private static IotsysPersistenceManager persistence = new HashMapPersistenceManager();

	private static ExecutorService pool = Executors.newCachedThreadPool();

	public static synchronized String createKey(String host, int port,
			String uri) {
		return host + ":" + port + "/" + uri;
	}

	public static synchronized void startPollRecording(String host, int port,
			String uri, int format, int protocol, long timeout)
			throws CommunicationException, HistoryExistsException {
		logger.info("starting poll recording of " + host + ":" + port + "/"
				+ uri + ", format: " + format + ", protocol: " + protocol);
		if (hasRecording(host, port, uri)) {
			logger.info("a worker with this uri is already active");
			throw new HistoryExistsException(
					"a worker with this uri is already active");
		}
		timeout = Math.max(timeout, TIMEOUT_MIN_POLL);
		if (timeout <= 0) {
			timeout = TIMEOUT_DEFAULT_POLL;
			logger.info("timeout set to: " + timeout);
		}
		ObjectPollWorker worker = null;
		try {
			worker = new ObjectPollWorker(host, port, uri, format, protocol,
					timeout, createKey(host, port, uri));
		} catch (Exception e) {
			if(e.getMessage() == null) {
				throw new CommunicationException(e.getClass().getName());
			}
			throw new CommunicationException(e.getMessage());
		}

		synchronized (activeWorkers) {
			activeWorkers.put(createKey(host, port, uri), worker);
		}
		pool.execute(worker);
	}

	public static synchronized void startWatchRecording(String host, int port,
			String uri, int format, int protocol)
			throws CommunicationException, HistoryExistsException {
		logger.info("starting watch recording of " + host + ":" + port + "/"
				+ uri + ", format: " + format + ", protocol: " + protocol);
		if (hasRecording(host, port, uri)) {
			logger.info("a worker with this uri is already active");
			throw new HistoryExistsException(
					"a worker with this uri is already active");
		}
		WatchPollWorker worker = null;
		try {
			worker = new WatchPollWorker(host, port, uri, format,
					protocol, TIMEOUT_DEFAULT_WATCH, createKey(host, port, uri));
		}catch(Exception e) {
			if(e.getMessage() == null) {
				throw new CommunicationException(e.getClass().getName());
			}
			throw new CommunicationException(e.getMessage());
		}
		synchronized (activeWorkers) {
			activeWorkers.put(createKey(host, port, uri), worker);
		}
		pool.execute(worker);
	}

	public static synchronized void startFeedRecording(String host, int port,
			String uri, int format, int protocol)
			throws CommunicationException, HistoryExistsException {
		logger.info("starting feed recording of " + host + ":" + port + "/"
				+ uri + ", format: " + format + ", protocol: " + protocol);
		if (hasRecording(host, port, uri)) {
			logger.info("a worker with this uri is already active");
			throw new HistoryExistsException(
					"a worker with this uri is already active");
		}

		FeedPollWorker worker = null;
		try {
			worker = new FeedPollWorker(host, port, uri, format, protocol,
					TIMEOUT_DEFAULT_WATCH, createKey(host, port, uri));
		} catch(Exception e) {
			logger.severe("could not start feed recording: " + e);
			if(e.getMessage() == null) {
				throw new CommunicationException(e.getClass().getName());
			}
			throw new CommunicationException(e.getMessage());
		}
		synchronized (activeWorkers) {
			activeWorkers.put(createKey(host, port, uri), worker);
		}
		pool.execute(worker);
	}

	public static synchronized void stopRecording(String host, int port,
			String uri) throws NoSuchHistoryException {
		String key = createKey(host, port, uri);
		synchronized (activeWorkers) {
			if (activeWorkers.containsKey(key)) {
				activeWorkers.get(key).shutdown();
				activeWorkers.remove(key);
			} else {
				throw new NoSuchHistoryException(
						"no history worker for given uri found");
			}
		}
	}

	public static synchronized List<IotHistoryRecord> getData(String host,
			int port, String uri) throws NoSuchHistoryException,
			CommunicationException {
		List<IotHistoryRecord> records = persistence
				.getHistoryRecords(createKey(host, port, uri));
		Collections.sort(records);
		return records;
	}

	public static synchronized void deleteData(String host, int port, String uri)
			throws CommunicationException {
		persistence.deleteHistoryRecords(createKey(host, port, uri));
	}

	public static synchronized boolean hasRecording(String host, int port,
			String uri) {
		synchronized (activeWorkers) {
			return activeWorkers.containsKey(createKey(host, port, uri));
		}
	}

	public static synchronized boolean hasData(String host, int port, String uri)
			throws CommunicationException {
		return persistence.hasRecords(createKey(host, port, uri));
	}

	private static abstract class HistoryWorker implements Runnable {

		private String host;
		private int port;
		private String uri;

		private long timeout;
		private long actualTimeout;
		private boolean shutdown = false;

		private String key;

		private TransportProtocol protocol;

		private HistoryWorker(String host, int port, String uri, int format,
				int protocol, long timeout, String key)
				throws InstantiationException, IllegalAccessException, IOException {
			this.host = host;
			this.port = port;
			this.uri = uri;
			this.timeout = timeout;
			this.actualTimeout = timeout;
			this.key = key;
			this.protocol = ProtocolRegistry.getProtocol(protocol);
			this.protocol.setTransportFormat(FormatRegistry.getFormat(format)); 
			logger.info(this.getClass().getSimpleName() + " initialized: host:"
					+ getHost() + ", port:" + getPort() + ", href:" + getUri()
					+ ", timeout:" + getTimeout());
		}

		@Override
		public void run() {
			while (!isShutdown()) {
				try {
					doWork();
					setDefaultTimeout();
				} catch (ObjectObsoleteException e) { 
					logger.warning("object at "
							+ this.host + ":" + this.port + "/" + this.uri
							+ " has become obsolete: " + e.getMessage());
					IotError error = new IotError();
					error.setName(e.getMessage());
					saveDatapoint(error);
					shutdown();
				} catch (CommunicationException e) {
					logger.warning("cannot qurey data for object at "
							+ this.host + ":" + this.port + "/" + this.uri
							+ ": " + e.getMessage());
					logger.warning("setting default error timeout");
					maybeSetErrorTimeout();
					IotError error = new IotError();
					error.setName(e.getMessage());
					saveDatapoint(error);
				}
				try {
					Thread.sleep(actualTimeout);
				} catch (InterruptedException e) {
					shutdown();
				}
			}
			logger.info(this.getClass().getSimpleName() + " shut down");
		}

		protected abstract void doWork() throws CommunicationException;

		private void maybeSetErrorTimeout() {
			actualTimeout = Math.max(timeout, TIMEOUT_COMM_ERROR);
		}

		private void setDefaultTimeout() {
			actualTimeout = timeout;
		}

		protected void saveDatapoint(IotObject value) {
			try {
				if (!(value instanceof IotHistoryRecord)) {
					IotHistoryRecord record = new IotHistoryRecord();
					IotAbsoluteTime timestamp = new IotAbsoluteTime();
					timestamp.setValue(System.currentTimeMillis());
					record.setTimestamp(timestamp);
					record.setValue(value);
					persistence.persistHistoryRecord(getKey(), record);
				} else {
					persistence.persistHistoryRecord(getKey(),
							(IotHistoryRecord) value);
				}
			} catch (CommunicationException e) {
				logger.warning("CommunicationException while persisting datapoint: "
						+ e.getMessage());
				IotHistoryRecord record = new IotHistoryRecord();
				IotAbsoluteTime timestamp = new IotAbsoluteTime();
				IotError error = new IotError();
				error.setName(e.getMessage());
				timestamp.setValue(System.currentTimeMillis());
				record.setTimestamp(timestamp);
				record.setValue(error);
				try {
					persistence.persistHistoryRecord(getKey(), record);
				} catch (CommunicationException e1) {
					logger.severe("CommunicationException while persisting error, cant really do anything...; "
							+ e.getMessage());
				}
			}
		}

		protected void saveDatapoints(List<IotObject> values) {
			for (IotObject o : values) {
				saveDatapoint(o);
			}
		}

		protected String getHost() {
			return host;
		}

		protected int getPort() {
			return port;
		}

		protected String getUri() {
			return uri;
		}

		protected void setTimeout(long timeout) {
			this.timeout = timeout;
			this.actualTimeout = timeout;
		}

		protected String getKey() {
			return key;
		}

		protected boolean isShutdown() {
			return shutdown;
		}

		public void shutdown() {
			logger.info("shutting down " + this.getClass().getSimpleName());
			this.shutdown = true;
		}

		protected TransportProtocol getProtocol() {
			return protocol;
		}

		protected long getTimeout() {
			return timeout;
		}

	}

	private static class ObjectPollWorker extends HistoryWorker {

		private ObjectPollWorker(String host, int port, String uri, int format,
				int protocol, long timeout, String key)
				throws InstantiationException, IllegalAccessException, IOException {
			super(host, port, uri, format, protocol, timeout, key);
		}

		@Override
		public void doWork() throws CommunicationException {
			logger.info("polling object: " + getUri());
			IotObject newValue;
			try {
				newValue = getProtocol().sendGetRequest(getHost(), getPort(),
						getUri());
				if (newValue instanceof IotError) {
					throw new CommunicationException(newValue.getName());
				}
				saveDatapoint(newValue);
			} catch (IOException e) {
				throw new CommunicationException(e.getMessage());
			}
		}

	}

	private static class WatchPollWorker extends HistoryWorker {

		private static final String URL_POLL = "/pollChanges";

		private WatchPollWorker(String host, int port, String uri, int format,
				int protocol, long timeout, String key)
				throws CommunicationException, InstantiationException,
				IllegalAccessException, IOException {
			super(host, port, uri, format, protocol, timeout, key);
			IotObject watchObject = null;
			try {
				watchObject = getProtocol().sendGetRequest(host, port, uri);
			} catch (IOException e) {
				throw new CommunicationException(e.getMessage());
			}
			if (!(watchObject instanceof IotWatch)) {
				throw new CommunicationException(host + ":" + port + "/" + uri
						+ " is not a watch object");
			}
			IotWatch watch = (IotWatch) watchObject;
			adaptTimeout(watch);
		}

		private WatchPollWorker(IotWatch watch, int format, int protocol,
				long timeout, String key) throws InstantiationException,
				IllegalAccessException, IOException {
			super(watch.getHost(), watch.getPort(), watch.getHref(), format,
					protocol, timeout, key);
			adaptTimeout(watch);
		}

		private void adaptTimeout(IotWatch watch) {
			IotRelativeTime lease = watch.getLeaseTime();
			if (lease.getValueInMillis() < getTimeout() && lease.isWritable()) {
				logger.info("trying to write lease value");
				long setTimeout = (long) (getTimeout() * 1.2);
				lease.setValue(setTimeout);
				try {
					IotObject newLease = getProtocol().sendPutRequest(
							getHost(), getPort(), lease.getHref(), lease);
					if (!(newLease instanceof IotRelativeTime)) {
						logger.info("writing lease value not successful, got: "
								+ newLease);
					}
				} catch (Exception e) {
					logger.warning("Exception while writing lease: "
							+ e.getMessage());
				}
			}
			setTimeout((long) (lease.getValueInMillis() * 0.8));
			logger.info("timeout set to: " + getTimeout());
		}

		private List<IotObject> getWatchChanges() throws CommunicationException {
			IotObject response;
			try {
				response = getProtocol().sendPostRequest(getHost(), getPort(),
						getUri() + URL_POLL, null);

				if (!(response instanceof IotWatchOut)) {
					throw new ObjectObsoleteException(
							"response not of type IotWatchOut");
				}
				return ((IotWatchOut) response).getValues();
			} catch (IOException e) {
				throw new ObjectObsoleteException(e.getMessage());
			}
		}

		@Override
		public void doWork() throws CommunicationException {
			logger.info("polling watch changes: " + getUri());
			List<IotObject> newValues = getWatchChanges();
			logger.info("watch out datapoints: " + newValues.size());
			for (IotObject newValue : newValues) {
				logger.info("saving new datapoint: " + newValue);
				saveDatapoint(newValue);
			}
		}

	}

	private static class FeedPollWorker extends HistoryWorker {

		private WatchPollWorker delegate;

		private FeedPollWorker(String host, int port, String uri, int format,
				int protocol, long timeout, String key)
				throws CommunicationException, InstantiationException,
				IllegalAccessException, IOException {
			super(host, port, uri, format, protocol, timeout, key);
			IotHistory history = getHistoryObject(host, port, uri);
			IotWatch watch = initializeWatch(host, port, history);
			delegate = new WatchPollWorker(watch, format, protocol, timeout,
					key);
		}

		private IotHistory getHistoryObject(String host, int port,
				String historyUri) throws CommunicationException {
			IotObject historyObject = null;
			try {
				historyObject = getProtocol().sendGetRequest(host, port,
						historyUri);
			} catch (IOException e) {
				throw new CommunicationException(e.getMessage());
			}
			if (!(historyObject instanceof IotHistory)) {
				throw new CommunicationException(host + ":" + port + "/"
						+ historyUri + " is not a history object");
			}
			return (IotHistory) historyObject;
		}

		private IotWatch initializeWatch(String host, int port,
				IotHistory history) throws CommunicationException {
			IotObject watchObject;
			try {
				watchObject = getProtocol().sendPostRequest(getHost(),
						getPort(), WATCH_SERVICE_URI, null);
			} catch (IOException e) {
				throw new CommunicationException(e.getMessage());
			}
			if (!(watchObject instanceof IotWatch)) {
				throw new CommunicationException(
						"could not create new watch for history feed");
			}
			IotWatch watch = (IotWatch) watchObject;
			logger.info("watch: " + watch + ", history: " + history);
			watch.addWatchObject(history.getFeed());
			return watch;
		}

		@Override
		public void doWork() throws CommunicationException {
			System.out.println("polling history feed changes: " + getUri());
			List<IotObject> watchOutObjects = delegate.getWatchChanges();

			System.out.println("watch out datapoints: "
					+ watchOutObjects.size());
			for (IotObject newValue : watchOutObjects) {
				if (newValue instanceof IotFeed) {
					List<IotObject> historyRecords = newValue
							.getChildren(IotHistoryRecord.class);
					System.out.println("saving new datapoints: "
							+ historyRecords);
					saveDatapoints(historyRecords);
				}
			}
		}

	}

}
