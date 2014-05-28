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

import com.google.appengine.spi.FactoryProvider;

/**
 * The structure of this class follows the code structure seen in the other
 * google APIs, an API registers a FactoryProvider with the interface of the
 * APIs ServiceFactory as argument, the FactoryProvider interface specifies
 * a method {@link FactoryProvider#getFactoryInstance()} which returns the
 * implementation of that interface.
 * 
 * This interface is implemented by {@link IotsysServiceFactoryImpl} and used in
 * {@link IotsysServiceFactoryProvider} to register the provider for use in
 * {@link IotsysServiceFactory}
 * 
 * @author Clemens Puehringer
 * 
 */
public interface IIotsysServiceFactory {

	/**
	 * Get an IotsysService instance initialized with the hostname and
	 * portnumber of the serve that this instance should communicate with, the
	 * transport format will default to
	 * {@link com.google.appengine.api.iotsys.comm.Format.OBIX_PLAINTEXT}; the
	 * transport protocol will default to
	 * {@link com.google.appengine.api.iotsys.comm.Protocol.COAP};
	 * 
	 * @param host
	 *            The hostname of the server that the new instance should
	 *            communicate with
	 * @param port
	 *            The portnumber of the server that the new instance should
	 *            communicate with
	 * @return An IotsysService instance initialized with hostname and
	 *         portnumber
	 */
	IotsysService getIotsysService(String host, int port);

	/**
	 * Get an IotsysService instance initialized with the hostname, portnumber
	 * the transport format that should be used and the transport protocol that
	 * should be used when communicating with the server the transport format
	 * must be the value of a constant declared in
	 * {@link com.google.appengine.api.iotsys.comm.Format.OBIX_PLAINTEXT}; the
	 * transport protocol must be the value of a constant declared in
	 * {@link com.google.appengine.api.iotsys.comm.Protocol.COAP};
	 * 
	 * @param host
	 *            The hostname of the server that the new instance should
	 *            communicate with
	 * @param port
	 *            The portnumber of the server that the new instance should
	 *            communicate with
	 * @param format
	 *            The format that should be used when communicating with the
	 *            server
	 * @param protocol
	 *            The protocol that should be used when communicating with the
	 *            server
	 * @return
	 */
	IotsysService getIotsysService(String host, int port, int format,
			int protocol);

}
