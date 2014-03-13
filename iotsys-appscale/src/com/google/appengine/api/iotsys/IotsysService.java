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

import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.appengine.api.iotsys.exception.HistoryExistsException;
import com.google.appengine.api.iotsys.exception.NoSuchHistoryException;
import com.google.appengine.api.iotsys.object.IotObject;
import com.google.appengine.api.iotsys.object.history.IotHistoryRecord;
import com.google.appengine.api.iotsys.object.watch.IotWatch;

/**
 * 
 * @author Clemens Puehringer
 * 
 */
public interface IotsysService {

	/**
	 * Queries the Server for the object at the given uri (href) and returns a
	 * local copy of the object
	 * 
	 * @param href
	 *            The uri of the object to retrieve from the server
	 * @return A local copy of the object
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object, the exception will contain the error object's name
	 *             (the error message) as its error message
	 */
	public IotObject retrieveObject(String href) throws CommunicationException;

	/**
	 * Creates a new "obix:Watch" object on the server and returns a local copy
	 * of the object, this object can then be used to modify the watch according
	 * to its contract
	 * 
	 * @return a local copy of the new watch object created on the server
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object
	 */
	public IotWatch createWatch() throws CommunicationException;

	/**
	 * creates a new "obix:Watch" object on the server using the given uri as
	 * the uri for the servers watchService, the "add" operation should not be
	 * included in the uri and will be invoked automatically
	 * 
	 * @param watchServiceUri
	 *            the uri of the watchService root (excluding the add operation)
	 * @return a local copy of the new watch object created on the server
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object
	 */
	public IotWatch createWatch(String watchServiceUri)
			throws CommunicationException;

	/**
	 * Starts recording a history for the given object which can be queried
	 * subsequently by calling {@link IotsysService#getHistoryData(IotObject)}.
	 * The object will be polled from the server regularly with the given
	 * interval in milliseconds, the interval can not be lower than 10000
	 * milliseconds to keep server load in check
	 * 
	 * @param toWatch
	 *            The object to record a history for, this object need only
	 *            contain the host, port and href of the actual object, it can
	 *            be created from scratch as long as these three fields are set;
	 *            it is not necessary to create it by using
	 *            {@link IotsysService#retrieveObject(String)}. The format and
	 *            protocol fields are optional and will default to
	 *            OBIX_PLAINTEXT and COAP
	 * @param interval
	 *            The interval in which the object polling should take place in
	 *            milliseconds, should be at least 10000 (10sec)
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object
	 * @throws HistoryExistsException
	 *             If a history for the given object is already being recorded.
	 */
	public void recordObject(IotObject object, long interval)
			throws CommunicationException, HistoryExistsException;

	/**
	 * Starts recording the output of the watch object by invoking its "query"
	 * operation, the returned objects will be timestamped with the local server
	 * time at query time, queries take place approximately all 30 seconds, if
	 * the watch has a shorter lease time than that, the API will try to set the
	 * lease time to over 30 seconds, if that is impossible because the lease is
	 * not writable, the query interval will be adjusted to a lower value
	 * conforming with the lease time
	 * 
	 * @param watch
	 *            The watch object to query regularly for new events, the object
	 *            itself need not be an IotWatch object but the uri the object
	 *            is pointing to must have an "obix:Watch" contract; the given
	 *            object has to have the host, port and href field set, the
	 *            format and protocol fields are optional and will default to
	 *            OBIX_PLAINTEXT and COAP
	 * @throws CommunicationException
	 *             If the given object's href does not point to a "obix:Watch"
	 *             object or the server is unreachable at creation time of the
	 *             new history worker thread
	 * @throws HistoryExistsException
	 *             If a history for the given watch is already being recorded.
	 */
	public void recordWatch(IotObject watch) throws CommunicationException,
			HistoryExistsException;

	/**
	 * Starts recording the feed of the given history by creating a watch on the
	 * gateway and adding the feed of the given history to the watch.
	 * 
	 * @param history
	 *            The history object to take the feed from, the object itself
	 *            need not be an IotHistory object but the uri the object is
	 *            pointing to must have an "obix:History" contract; the given
	 *            object has to have the host, port and href field set, the
	 *            format and protocol fields are optional and will default to
	 *            OBIX_PLAINTEXT and COAP
	 * @throws CommunicationException
	 *             If the given object's href does not point to a "obix:History"
	 *             object or the server is unreachable at creation time of the
	 *             new history worker thread
	 * @throws HistoryExistsException
	 *             If a history for the given history-feed is already being
	 *             recorded.
	 */
	public void recordHistory(IotObject history) throws CommunicationException,
			HistoryExistsException;

	/**
	 * Returns available history records for the object, identified by the
	 * objects host, port and href; all other fields in the object are optional
	 * for this method
	 * 
	 * @param object
	 *            an IotsysObject with its host, port and href fields set
	 * @return all history records recorded for the given object
	 * @throws CommunicationException
	 *             If the persistence service encounters a communication problem
	 * @throws NoSuchHistoryException
	 *             If no history for the given object has been recorded yet
	 */
	public List<IotHistoryRecord> getHistoryData(IotObject object)
			throws CommunicationException, NoSuchHistoryException;

	/**
	 * Checks if a history is currently being recorded for the given object,
	 * identified by the objects host, port and href fields
	 * 
	 * @param object
	 *            an IotsysObject with its host, port and href fields set
	 * @return true if a history is currently being recorded for this object,
	 *         false otherwise
	 * @throws CommunicationException
	 *             If there is a problem communicating with the history service
	 */
	public boolean hasActiveHistoryRecording(IotObject object)
			throws CommunicationException;

	/**
	 * Checks if there are saved records for the given object, identified by the
	 * objects host, port and href fields
	 * 
	 * @param object
	 *            an IotsysObject with its host, port and href fields set
	 * @return true if there are saved history records for this object, false
	 *         otherwise
	 * @throws CommunicationException
	 *             If there is a problem communicating with the history service
	 *             or the persistence service
	 */
	public boolean hasHistoryData(IotObject object)
			throws CommunicationException;

	/**
	 * Stops recording a history for the given object, the object must have its
	 * host, port, and href fields set
	 * 
	 * @param object
	 *            an IotsysObject with its host, port and href fields set
	 * @throws CommunicationException
	 *             If there is a problem communicating with the history service
	 * @throws NoSuchHistoryException
	 *             If there is currently no active history recording for the
	 *             given object
	 */
	public void stopHistoryRecording(IotObject object)
			throws CommunicationException, NoSuchHistoryException;

	/**
	 * Deletes all history records for the given object, the object must have
	 * its host, port, and href fields set
	 * 
	 * @param object
	 *            an IotsysObject with its host, port and href fields set
	 * @throws CommunicationException
	 *             If there is a problem communicating with the history service
	 *             or the persistence service
	 */
	public void deleteHistoryRecords(IotObject object)
			throws CommunicationException;

}
