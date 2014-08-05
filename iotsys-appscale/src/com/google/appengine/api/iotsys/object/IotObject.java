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

package com.google.appengine.api.iotsys.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import obix.Contract;
import obix.Obj;
import obix.Status;
import obix.Uri;

import com.google.appengine.api.iotsys.IotsysConnectionProxy;
import com.google.appengine.api.iotsys.IotsysService;
import com.google.appengine.api.iotsys.comm.Format;
import com.google.appengine.api.iotsys.comm.Protocol;
import com.google.appengine.api.iotsys.exception.CommunicationException;
import com.google.apphosting.api.IotsysServicePb;

/**
 * Class for storing the data of an obix.Obj
 * 
 * @author Clemens Pühringer
 * 
 */
public class IotObject implements Serializable {

	private static final long serialVersionUID = 1L;

	protected String host;
	protected int port;
	private int transportFormat = Format.OBIX_PLAINTEXT;
	private int transportProtocol = Protocol.COAP;

	protected IotObject parent;
	protected List<IotObject> children;

	protected String name;
	protected String displayName;
	protected String display;
	protected String href;
	protected List<String> contract;
	protected boolean writable;
	protected String status;

	public IotObject() {
		children = new ArrayList<IotObject>();
	}

	/**
	 * Initialize the object from a given obix.Obj
	 * 
	 * @param obj
	 *            the obj to take the data from
	 */
	public void initialize(Obj obj) {
		this.setName(obj.getName());
		this.setDisplayName(obj.getDisplayName());
		this.setDisplay(obj.getDisplay());
		if (obj.getHref() != null) {
			this.setHref(obj.getHref().get());
		}
		if (obj.getIs() != null) {
			this.setContract(obj.getIs().toString());
		}

		this.setWritable(obj.isWritable());

		if (obj.getStatus() != null) {
			this.setStatus(obj.getStatus().toString());
		}
	}

	/**
	 * Initialize the object from a given protocol buffer
	 * 
	 * @param protobuf
	 *            the protocol buffer to take the data from
	 */
	public void initialize(IotsysServicePb.IotObjectProto protobuf) {
		if(protobuf.hasHost()) {
			this.setHost(protobuf.getHost());
		}
		if(protobuf.hasPort()) {
			this.setPort(protobuf.getPort());
		}
		if(protobuf.hasHref()) {
			this.setHref(protobuf.getHref());
		}
		if(protobuf.hasFormat()) {
			this.setFormat(protobuf.getFormat());
		}
		if(protobuf.hasProtocol()) {
			this.setProtocol(protobuf.getProtocol());
		}
		this.setName(protobuf.getName());
		this.setDisplayName(protobuf.getDisplayName());
		this.setDisplay(protobuf.getDisplay());
		this.setContract(protobuf.getContract());
		this.setWritable(protobuf.getWritable());
		this.setStatus(protobuf.getStatus());
	}

	/**
	 * do post initialize configuration
	 */
	public void postInit() {
	}

	/**
	 * write the data of this object to a given obix.Obj
	 * 
	 * @param obj
	 *            the object to write the data to
	 */
	public void writeToObj(Obj obj) {
		if (this.getName() != null) {
			obj.setName(this.getName());
		}
		if (this.getDisplayName() != null) {
			obj.setDisplayName(this.getDisplayName());
		}
		if (this.getDisplay() != null) {
			obj.setDisplay(this.getDisplay());
		}
		if (this.getHref() != null) {
			obj.setHref(new Uri(this.getHref()));
		}
		if (this.getContractString() != null) {
			obj.setIs(new Contract(this.getContractString()));
		}
		obj.setWritable(this.isWritable());
		if (this.getStatus() != null) {
			obj.setStatus(Status.parse(this.getStatus()));
		}
	}

	/**
	 * write the data of this object to a given protocol buffer builder
	 * 
	 * @param protoBuilder
	 *            the protocol buffer builder to write this objects data to
	 */
	public void writeToProtobuf(
			IotsysServicePb.IotObjectProto.Builder protoBuilder) {
		if (this.getHost() != null) {
			protoBuilder.setHost(this.getHost());
		}
		if (this.getPort() != 0) {
			protoBuilder.setPort(this.getPort());
		}
		if (this.getName() != null) {
			protoBuilder.setName(this.getName());
		}
		if (this.getDisplayName() != null) {
			protoBuilder.setDisplayName(this.getDisplayName());
		}
		if (this.getDisplay() != null) {
			protoBuilder.setDisplay(this.getDisplay());
		}
		if (this.getHref() != null) {
			protoBuilder.setHref(this.getHref());
		}
		if (this.getContractString() != null) {
			protoBuilder.setContract(this.getContractString());
		}
		protoBuilder.setWritable(this.isWritable());
		if (this.getStatus() != null) {
			protoBuilder.setStatus(this.getStatus());
		}
		protoBuilder.setFormat(getFormat());
		protoBuilder.setProtocol(getProtocol());
	}

	/**
	 * set the hostname or ip address of the server that this object resides on,
	 * the host, port and href fields must be set before invoking
	 * {@link IotObject#refresh()} or {@link IotObject#write()}. The host will
	 * be set automatically when creating the object through
	 * {@link IotsysService#retrieveObject(String)}
	 * 
	 * @param host
	 *            the hostname or ip address of the server that this object
	 *            resides on
	 */
	public void setHost(String host) {
		if (host == null || host.length() <= 0) {
			throw new IllegalArgumentException(
					"host must not be null and have a length > 0");
		}
		this.host = host;
	}

	/**
	 * @return The hostname of the server this object resides on
	 */
	public String getHost() {
		return host;
	}

	/**
	 * set the port of the server that this object resides on, the host, port
	 * and href fields must be set before invoking {@link IotObject#refresh()}
	 * or {@link IotObject#write()}. The port will be set automatically when
	 * creating the object through {@link IotsysService#retrieveObject(String)}
	 * 
	 * @param port
	 *            The port of the server that this object resides on
	 */
	public void setPort(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException(
					"port must be a valid port number [1-65535]");
		}
		this.port = port;
	}

	/**
	 * @return The port of the server that this object resides on
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the transport format that should be used to communicate with the
	 * server that this object resides on, must be one of the constants
	 * specified in {@link com.google.appengine.api.iotsys.comm.Format}
	 * 
	 * @param format
	 *            The transport format that should be used.
	 */
	public void setFormat(int format) {
		if (!Format.isValid(format)) {
			throw new IllegalArgumentException(
					"format must be a valid format constant declared in Format");
		}
		this.transportFormat = format;
	}

	/**
	 * Get the transport format that is used to communicate with the server that
	 * this object resides on, will usually be one of the constants specified in
	 * {@link com.google.appengine.api.iotsys.comm.Format}
	 * 
	 * @return The transport format that is used.
	 */
	public int getFormat() {
		return transportFormat;
	}

	/**
	 * Set the transport protocol that should be used to communicate with the
	 * server that this object resides on, must be one of the constants
	 * specified in {@link com.google.appengine.api.iotsys.comm.Protocol}
	 * 
	 * @param protocol
	 *            The transport protocol that should be used.
	 */
	public void setProtocol(int protocol) {
		if (!Protocol.isValid(protocol)) {
			throw new IllegalArgumentException(
					"protocol must be a valid protocol constant declared in Protocol");
		}
		this.transportProtocol = protocol;
	}

	/**
	 * Get the transport protocol that is used to communicate with the server
	 * that this object resides on, will usually be one of the constants
	 * specified in {@link com.google.appengine.api.iotsys.comm.Protocol}
	 * 
	 * @return The transport protocol that is used.
	 */
	public int getProtocol() {
		return transportProtocol;
	}

	/**
	 * Set the name of the object, the name is sometimes important in contracts,
	 * otherwise there is no benefit from manually changing the name of the
	 * object, as it will be overridden when {@link IotObject#refresh()} is
	 * called.
	 * 
	 * @param name
	 *            The new name of the object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the object.
	 * 
	 * @return The name of the object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the display name of the object, can be used to label the object,
	 * although the display name will only change locally and can not be pushed
	 * to the gateway server, so the display name of this object will only be
	 * changed for this local instance of IotObject.
	 * 
	 * @param dName
	 *            The display name of the object
	 */
	public void setDisplayName(String dName) {
		this.displayName = dName;
	}

	/**
	 * Get the display name of the object.
	 * 
	 * From the Obix specification:
	 * 
	 * The displayName facet provides a localized human readable name of the
	 * object stored as a xs:string
	 * 
	 * @return The display name of the object.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display of this object, the display is a human readable string of
	 * the status of the object, taking the current value of the object into
	 * account, a local write to the display field will not affect the actual
	 * object on the remote gateway
	 * 
	 * @param display
	 *            The new display string of the object
	 */
	public void setDisplay(String display) {
		this.display = display;
	}

	/**
	 * Get the display of this object
	 * 
	 * From the Obix specification:
	 * 
	 * The display facet provides a localized human readable description of the
	 * object stored as a xs:string:
	 * 
	 * @return
	 */
	public String getDisplay() {
		return display;
	}

	/**
	 * Set the href of this object, can be absolute or relative to a parent
	 * object.
	 * 
	 * @param href
	 *            The href of the object.
	 */
	public void setHref(String href) {
		this.href = href;
	}

	/**
	 * Get the absolute href of this object.
	 * 
	 * @return The absolute href of this object.
	 */
	public String getHref() {
		if (parent == null) {
			return href;
		}
		if (href == null) {
			return null;
		}
		/* href is absolute */
		if (href.startsWith("/")) {
			return href;
		}
		/* no parent href, this.href is the only information available */
		if (parent.getHref() == null) {
			return href;
		}
		if (parent.getHref().endsWith("/")) {
			return parent.getHref() + href;
		} else {
			return parent.getHref() + "/" + href;
		}
	}

	/**
	 * Set the contract(s) of this object using a space separated list of Obix
	 * contracts.
	 * 
	 * @param contract
	 *            A space separated list of obix contracts.
	 */
	public void setContract(String contract) {
		this.contract = new ArrayList<String>();
		String[] contractParts = contract.split(" ");
		for (String s : contractParts) {
			this.contract.add(s);
		}
	}

	/**
	 * Get this object's contract(s).
	 * 
	 * @return This object's contract(s).
	 */
	public List<String> getContract() {
		return contract;
	}

	/**
	 * Get a space separated string of this objects contracts.
	 * 
	 * @return A space separated string of this objects contracts.
	 */
	public String getContractString() {
		if (contract == null) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < contract.size() - 1; i++) {
			builder.append(contract.get(i));
			builder.append(' ');
		}
		builder.append(contract.get(contract.size() - 1));
		return builder.toString();
	}

	/**
	 * Set this object's writable flat, will have no effect on the actual object
	 * on the gateway.
	 * 
	 * @param writable
	 *            The new value of the writable flag.
	 */
	public void setWritable(boolean writable) {
		this.writable = writable;
	}

	/**
	 * Get this objects writable flag.
	 * 
	 * @return true if the object is writable, false otherwise.
	 */
	public boolean isWritable() {
		return writable;
	}

	/**
	 * Set the status of this object, will have no effect on the actual object
	 * on the gateway.
	 * 
	 * @param status
	 *            The new status of this object.
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get the status of this object.
	 * 
	 * @return The status of this object.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Add a child to this object and set the given objects parent to this
	 * object.
	 * 
	 * @param child
	 *            The new child to add.
	 */
	public void addChild(IotObject child) {
		child.setParent(this);
		this.children.add(child);
	}

	/**
	 * Get the child of this object with the given name.
	 * 
	 * @param name
	 *            The name of the child object to find
	 * @return The child with the given name or null if no such child was found
	 */
	public IotObject getChild(String name) {
		for (IotObject child : children) {
			if (name.equals(child.getName())) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Get a list of all the children of this object.
	 * 
	 * @return A list of all the children of this object.
	 */
	public List<IotObject> getAllChildren() {
		return children;
	}

	/**
	 * Set this objects parent to the given object.
	 * 
	 * @param parent
	 *            The new parent of this object.
	 */
	public void setParent(IotObject parent) {
		this.parent = parent;
		resolveHref(parent.getHref());
	}
	
	/**
	 * Tries to figure out the
	 * new relative href of the child object, if for example the parent has a
	 * href "/object1" and the child has a href "object1/value", then after this
	 * method, the child will have a new href "value".
	 * 
	 * @param parentHref the parent's href
	 */
	protected void resolveHref(String parentHref) {
		if (this.href != null && parent.getHref() != null) {
			if (this.href.startsWith("/")) {
				return;
			}
			if (this.href.contains("/")) {
				String firstPart = href.substring(0, href.indexOf('/'));
				if (parent.getHref().endsWith(firstPart)
						|| parent.getHref().endsWith(firstPart + "/")) {
					href = href.substring(href.indexOf('/') + 1);
				} else {
					href = href.substring(href.lastIndexOf('/') + 1);
				}
			}
		}
	}

	/**
	 * Get the parent object of this object.
	 * 
	 * @return The parent of this object.
	 */
	public IotObject getParent() {
		return parent;
	}

	/**
	 * Get all children of this object with the given class.
	 * 
	 * @param clazz
	 *            The class to match children against.
	 * @return A list of children which are of class clazz.
	 */
	public List<IotObject> getChildren(Class<? extends IotObject> clazz) {
		List<IotObject> objects = new ArrayList<IotObject>();
		for (IotObject o : children) {
			if (clazz.isAssignableFrom(o.getClass())) {
				objects.add(o);
			}
		}
		return objects;
	}

	/**
	 * Refreshes the object by sending a GET request with this objects
	 * communication information (host, port, href, format, protocol) and
	 * merging the returned object with this one.
	 * 
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object.
	 */
	public void refresh() throws CommunicationException {
		IotObject response = IotsysConnectionProxy.getInstance()
				.sendGetRequest(this.getHost(), this.getPort(), this.getHref(),
						getFormat(), getProtocol());
		if (response instanceof IotError) {
			throw new CommunicationException(response.getDisplay());
		}
		if (!merge(response)) {
			throw new CommunicationException("could not merge "
					+ this.getClass().getSimpleName() + " with "
					+ response.getClass().getSimpleName());
		}
	}

	/**
	 * Write this object to the server by sending a PUT request with this
	 * objects communication information (host, port, href, format, protocol)
	 * and merging the returned object with this one.
	 * 
	 * @throws CommunicationException
	 *             If the server is unreachable or returns an "obix:Error"
	 *             object.
	 */
	public void write() throws CommunicationException {
		IotObject response = IotsysConnectionProxy.getInstance()
				.sendPutRequest(this.getHost(), this.getPort(), this.getHref(),
						getFormat(), getProtocol(), this);
		if (response instanceof IotError) {
			throw new CommunicationException(response.getDisplay());
		}
		if (!merge(response)) {
			throw new CommunicationException("could not merge "
					+ this.getClass().getSimpleName() + " with "
					+ response.getClass().getSimpleName());
		}
	}

	/**
	 * sets this object's fields to the field values of the given object
	 * 
	 * @param object
	 *            The object to take the new values from.
	 * @return true if the merging was successful, false if the objects could
	 *         not be merged due to class discrepancies, etc.
	 */
	protected boolean merge(IotObject object) {
		this.children.clear();
		for(IotObject o : object.getAllChildren()) {
			this.addChild(o);
		}
		this.name = object.getName();
		this.displayName = object.getDisplayName();
		this.display = object.getDisplay();
		this.contract = object.getContract();
		this.writable = object.isWritable();
		this.status = object.getStatus();
		postInit();
		return true;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName());
		if(host != null)
			builder.append(": host:").append(host);
		if(port > 0)
			builder.append(", port:").append(port);
		if(name != null)
			builder.append(", name:").append(name);
		if(displayName != null)
			builder.append(", displayname:").append(displayName);
		if(display != null)
			builder.append(", display:").append(display);
		if(getHref() != null)
			builder.append(", href:").append(getHref());
		if(contract != null)
			builder.append(", contract:").append(contract);
		if(writable)
			builder.append(", writable:").append(writable);
		if(status != null)
			builder.append(", status:").append(status);
		if(children.size() > 0)
			builder.append(", children:").append(children.size());
		return builder.toString();
	}

}
