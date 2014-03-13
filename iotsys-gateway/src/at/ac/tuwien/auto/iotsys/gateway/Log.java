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
package at.ac.tuwien.auto.iotsys.gateway;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.NanoHTTPD;

import ch.ethz.inf.vs.californium.coap.EndpointAddress;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.ObservingManager;
import ch.ethz.inf.vs.californium.coap.TokenManager;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.layers.Layer;
import ch.ethz.inf.vs.californium.util.Properties;

/**
 * Logging configuration.
 */
public class Log {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Level logLevel = Level.parse(PropertiesLoader.getInstance().getProperties().getProperty("iotsys.gateway.loglevel", "FINEST"));
	
	private static final Formatter printFormatter = new Formatter() {
		@Override
		public String format(LogRecord record) {
			return String.format("%s [%s] %s - %s\r\n",
								 dateFormat.format(new Date(record.getMillis())),
								 record.getSourceClassName().replace("ch.ethz.inf.vs.californium.", ""),
								 record.getLevel(), record.getMessage()
								);
		}
	};
	
	private static final Formatter csvFormatter = new Formatter() {
		@Override
		public String format(LogRecord record) {
			return String.format("%s\r\n",
								 record.getMessage()
								);
		}
	};
	
	public static void setLevel(Level l) {
		logLevel = l;
	}

	public static void init() {
		
		Logger globalLogger = Logger.getLogger("");
		
		// Remove the default handler
		for (Handler handler : globalLogger.getHandlers()) {
		    globalLogger.removeHandler(handler);
		}
		
		// create custom console handler
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setFormatter(printFormatter);
		// set logging level
		cHandler.setLevel(logLevel);
		// add
		globalLogger.addHandler(cHandler);
		globalLogger.setLevel(logLevel);
		
		// create custom file handler
		FileHandler fHandler;
		try {
			fHandler = new FileHandler("iotsys-log.%g.txt", true);
			fHandler.setFormatter(printFormatter);
			globalLogger.addHandler(fHandler);
		} catch (Exception e) {
			globalLogger.severe("Cannot add file logger: " + e.getMessage());
		}
		
		FileHandler knxHandler;
		
		try{
			knxHandler = new FileHandler("KNX.txt",true);
			knxHandler.setFormatter(csvFormatter);
			Logger.getLogger("knxbus").addHandler(knxHandler);
			Logger.getLogger("knxbus").setLevel(logLevel);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// customize levels
		Logger.getLogger(Endpoint.class.getName()).setLevel(logLevel);
		Logger.getLogger(EndpointAddress.class.getName()).setLevel(logLevel);
		Logger.getLogger(LinkFormat.class.getName()).setLevel(logLevel);
		Logger.getLogger(Message.class.getName()).setLevel(logLevel);
		Logger.getLogger(TokenManager.class.getName()).setLevel(logLevel);
		Logger.getLogger(ObservingManager.class.getName()).setLevel(logLevel);
		Logger.getLogger(Layer.class.getName()).setLevel(logLevel);
		Logger.getLogger(Properties.class.getName()).setLevel(logLevel);
								
		// obix server logger
		
		Logger.getLogger(IoTSySGateway.class.getName()).setLevel(logLevel);
	
		
		
		// indicate new start-up
		Logger.getLogger(Log.class.getName()).info("==[ START-UP ]========================================================");
		Logger.getLogger(Log.class.getName()).finest("==[ Finest ]========================================================");

		Logger.getLogger(NanoHTTPD.class
				.getName()).setLevel(Level.FINEST);
	}
}

