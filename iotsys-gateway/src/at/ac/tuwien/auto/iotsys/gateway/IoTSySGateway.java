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

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.util.EvaluationGenerateContracts;
import at.ac.tuwien.auto.iotsys.gateway.util.EvaluationUtil;

import com.csvreader.CsvWriter;

import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.CoAPServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.NanoHTTPD;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixObservingManager;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServerImpl;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;

/**
 * Standalone class to launch the gateway.
 * 
 */
public class IoTSySGateway {
	private ObjectBroker objectBroker;
	private DeviceLoaderImpl deviceLoader;
	
	
	private ArrayList<Connector> connectors = new ArrayList<Connector>();

	private static final Logger log = Logger.getLogger(IoTSySGateway.class
			.getName());

	private ObixServer obixServer = null;

	public IoTSySGateway() {

	}

	public void startGateway() {

		Log.init();
		log.info("Server starting.");
		at.ac.tuwien.auto.iotsys.gateway.obix.objects.ContractInit.init();

		String httpPort = PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.gateway.http.port", "8080");

		// initialize object broker
		objectBroker = ObjectBrokerImpl.getInstance();
		obixServer = new ObixServerImpl(objectBroker);

		// add initial objects to the database
		deviceLoader = new DeviceLoaderImpl();
		connectors = deviceLoader.initDevices(objectBroker);

		ObixObservingManager.getInstance().setObixServer(obixServer);

		try{
			new CoAPServer(obixServer);
			new NanoHTTPD(Integer.parseInt(httpPort), obixServer);
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	public void stopGateway() {
		objectBroker.shutdown();
		closeConnectors();
	}

		
	public static void main(String[] args) {
		final IoTSySGateway iotsys = new IoTSySGateway();

		iotsys.startGateway();
		
		//EvaluationUtil.evaluation();
				
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		try {
			in.read();
			iotsys.stopGateway();
		} catch (IOException ioex) {
			ioex.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}

	private void closeConnectors() {
		for (Connector connector : connectors) {
			try {
				connector.disconnect();
				log.info("Shutting down connector " + connector.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
