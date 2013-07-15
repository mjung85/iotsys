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
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.MDnsResolver;
import at.ac.tuwien.auto.iotsys.commons.interceptor.ClassAlreadyRegisteredException;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.gateway.interceptor.InterceptorBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.CoAPServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.NanoHTTPD;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixObservingManager;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServerImpl;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.mdnssd.Named;
import at.ac.tuwien.auto.iotsys.mdnssd.MDnsResolverImpl;
import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptorSettings;
// import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptor;
/**
 * Standalone class to launch the gateway.
 * 
 */
public class IoTSySGateway {
	private ObjectBroker objectBroker;
	private DeviceLoaderImpl deviceLoader;
	private InterceptorBroker interceptorBroker;

	private boolean osgiEnvironment = false;
	
	private ArrayList<Connector> connectors = new ArrayList<Connector>();

	private static final Logger log = Logger.getLogger(IoTSySGateway.class
			.getName());

	private ObixServer obixServer = null;
	
	private MDnsResolver mdnsResolver;

	public IoTSySGateway() {

	}

	public void startGateway() {

		Log.init();
		log.info("Server starting.");
		
		// init exi util
		ExiUtil.getInstance();
		// init contracts
		at.ac.tuwien.auto.iotsys.gateway.obix.objects.ContractInit.init();

		String httpPort = PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.gateway.http.port", "8080");

		log.info("HTTP-Port: " + httpPort);
		
		// initialize object broker
		objectBroker = ObjectBrokerImpl.getInstance();
		obixServer = new ObixServerImpl(objectBroker);
		
		objectBroker.setMdnsResolver(mdnsResolver);

		// add initial objects to the database
		deviceLoader = new DeviceLoaderImpl();
		connectors = deviceLoader.initDevices(objectBroker);

		log.info("No of records built: " + objectBroker.getMDnsResolver().getNumberOfRecord());
		
		interceptorBroker = InterceptorBrokerImpl.getInstance();
		// initialize interceptor broker
		boolean enableXacml = Boolean.parseBoolean(PropertiesLoader.getInstance()
				.getProperties().getProperty("iotsys.gateway.xacml", "false"));

		log.info("XACML module enabled: " + enableXacml);
		if (enableXacml && !isOsgiEnvironment()) {			
			// temporarly register interceptor
			try {
				// load PDP interceptor if available on class path
				// load settings for remote pdp
				boolean remotePdp = Boolean.parseBoolean(PropertiesLoader
						.getInstance().getProperties()
						.getProperty("iotsys.gateway.xacml.remotePDP", "false"));
				// PDPInterceptorSettings.getInstance().setRemotePdp(remotePdp);

				String remotePdpWsdl = PropertiesLoader.getInstance()
						.getProperties()
						.getProperty("iotsys.gateway.xacml.remotePDPWsdl", "");
				// PDPInterceptorSettings.getInstance().setRemotePdpWsdl(remotePdpWsdl);
				Class pdpSettingsClazz = null;
				
				try {
					pdpSettingsClazz = Class.forName("at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptorSettings");
					log.info("Class found: " + pdpSettingsClazz.getName());
				} catch (ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				

				if (pdpSettingsClazz != null) {
					
						Method getInstance;
						try {
							getInstance = pdpSettingsClazz.getDeclaredMethod("getInstance");
							PDPInterceptorSettings settings = (PDPInterceptorSettings) getInstance.invoke(null, null);
							settings.setRemotePdpWsdl(remotePdpWsdl);
							settings.setRemotePdp(remotePdp);
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				

				}
				
				
				Class clazz = null;
				try {
					clazz = Class.forName("at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptor");
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
				if(clazz != null){
					Interceptor interceptor;
					try {
						interceptor = (Interceptor) clazz.newInstance();
						interceptorBroker.register(interceptor);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} catch (ClassAlreadyRegisteredException e) {
				// silent exceptionhandling
			}
		}

		ObixObservingManager.getInstance().setObixServer(obixServer);

		try {
			new CoAPServer(obixServer);
			new NanoHTTPD(Integer.parseInt(httpPort), obixServer);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void stopGateway() {
		objectBroker.shutdown();
//		CsvCreator.instance.close();
		closeConnectors();
	}

	public static void main(String[] args) {
		final IoTSySGateway iotsys = new IoTSySGateway();
		(new Named()).startNamedService();
		iotsys.setMdnsResolver(MDnsResolverImpl.getInstance());
		iotsys.startGateway();

		// TestClient testClient = new TestClient(iotsys.objectBroker);
		// testClient.runTests();

		// EvaluationUtil.evaluation();

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

	public boolean isOsgiEnvironment() {
		return osgiEnvironment;
	}

	public void setOsgiEnvironment(boolean osgiEnvironment) {
		this.osgiEnvironment = osgiEnvironment;
	}
	
	public ObjectBroker getObjectBroker() {
		return objectBroker;
	}	

	public MDnsResolver getMdnsResolver() {
		return mdnsResolver;
	}

	public void setMdnsResolver(MDnsResolver mdnsResolver) {
		this.mdnsResolver = mdnsResolver;
	}
}
