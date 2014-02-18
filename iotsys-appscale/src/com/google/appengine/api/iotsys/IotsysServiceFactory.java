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

import com.google.appengine.spi.ServiceFactoryFactory;

/**
 * The structure of this class follows the code structure seen in the other
 * google APIs, an API registers a FactoryProvider with the interface of the
 * APIs ServiceFactory as argument, the FactoryProvider interface specifies a
 * method {@link FactoryProvider#getFactoryInstance()} which returns the
 * implementation of that interface,
 * {@link com.google.appengine.spi.ServiceFactoryFactory} stores all the
 * FactoryProviders and is queried by the actual ServiceFactory for an instance
 * of the factory.
 * 
 * This class, like other API ServiceFactories retrieves an actual
 * I<API name>ServiceFactory implementation by querying 
 * {@link com.google.appengine.spi.ServiceFactoryFactory} for an instance of
 * {@link IIotsysServiceFactory} which was registered in
 * {@link IotsysServiceFactoryProvider}
 * 
 * @author Clemens Puehringer
 * 
 */
public class IotsysServiceFactory {

	/**
	 * Creates an instance of an IotsysService with set host and port, format
	 * will default to
	 * {@link com.google.appengine.api.iotsys.comm.Format.OBIX_PLAINTEXT} and
	 * protocol will default to
	 * {@link com.google.appengine.api.iotsys.comm.Protocol.COAP}
	 * 
	 * @param host
	 *            The host this instance should use to connect to
	 * @param port
	 *            The port of the server this service-instance should use
	 * @return A new IotsysServie instance initialized with the given host and
	 *         port
	 */
	public static IotsysService getIotsysService(String host, int port) {
		return getFactory().getIotsysService(host, port);
	}

	/**
	 * Creates an instance of an IotsysService with set host, port, format and
	 * protocol
	 * 
	 * @param host
	 *            The host this instance shoudl use to connect to
	 * @param port
	 *            The port of the server this service-instance should use
	 * @param format
	 *            The format that should be used when communicating with the
	 *            server, must be a constant from
	 *            {@link com.google.appengine.api.iotsys.comm.Format}
	 * @param protocol
	 *            The protocol that should be used when communicating with the
	 *            server, must be a constant from
	 *            {@link com.google.appengine.api.iotsys.comm.Protocol}
	 * @return A new IotsysServie instance initialized with the given values
	 */
	public static IotsysService getIotsysService(String host, int port,
			int format, int protocol) {
		return getFactory().getIotsysService(host, port, format, protocol);
	}

	private IotsysServiceFactory() {
	}

	private static IIotsysServiceFactory getFactory() {
		return ServiceFactoryFactory.getFactory(IIotsysServiceFactory.class);
	}
}
