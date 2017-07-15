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

package at.ac.tuwien.auto.iotsys.gateway.obix.server;

import java.net.*;

import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.persistent.UIDb;
import obix.Obj;

public interface ObixServer
{
	/**
	 *  Constant name for the default used protocol type by obix Server.   
	 */
	public static final String DEFAULT_OBIX_URL_PROTOCOL ="http";
	
	public String getIPv6LinkedHref(String ipv6Address);
	public boolean containsIPv6(String ipv6Address);
	
	/**
	 * The object specified via the URI is read by pulling it from the object broker, if the
	 * user has the right to do so.
	 * 
	 * @param href
	 *            URI of the object to be read.
	 * @param user
	 *            Name of the user who wants to read the object.
	 * @return XML representation of the object to be read.
	 */
	public Obj readObj(URI href, boolean refreshObject);

	/**
	 * The object specified via the URI is written by pushing it to the object
	 * broker.
	 * 
	 * @param href
	 *            URI of the object to be written
	 * @param xmlStream
	 *            XML representation of the object to be written.
	 * @return XML representation of the written object.
	 */
	public Obj writeObj(URI href, String xmlStream);
	public Obj applyObj(URI uri, String dataStream);

	/**
	 * The operation specified via the URI is invoked by pushing it to the
	 * object broker.
	 * 
	 * @param href
	 *            URI of the operation to be invoked.
	 * @param xmlStream
	 *            Parameters of the operation to be invoked.
	 * @return XML representation of the output parameters of the operation.
	 */
	public Obj invokeOp(URI href, String xmlStream);
	
	/**
	 * Get the absolute normalized path of this referred object.
	 * Returns null if this object doesn't exist.
	 */
	public String getNormalizedPath(String href);
	
	public String getCoRELinks();
	public ObjectBroker getObjectBroker();
	public UIDb getUidb();
	
	/**
	 * Get the hred of an obj with the corresponding qrcode
	 * @param qrcode
	 * @return href
	 */
	public String getQRCode(String qrcode);
}
