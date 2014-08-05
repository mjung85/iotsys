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
import java.util.logging.Logger;

import javax.servlet.ServletException;

import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.MdnsResolver;
import at.ac.tuwien.auto.iotsys.commons.Named;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.commons.ObjectBrokerHelper;
import at.ac.tuwien.auto.iotsys.commons.PropertiesLoader;
import at.ac.tuwien.auto.iotsys.commons.interceptor.ClassAlreadyRegisteredException;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.ContractInit;
import at.ac.tuwien.auto.iotsys.gateway.interceptor.InterceptorBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.CoAPServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixObservingManager;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.ObixServerImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.TomcatServer;
import at.ac.tuwien.auto.iotsys.gateway.obix.server.TomcatServerNoSecurity;
import at.ac.tuwien.auto.iotsys.gateway.util.ExiUtil;
import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptorSettings;

// import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptor;
/**
 * Standalone class to launch the gateway.
 * 
 */
public class IoTSySGateway {
	private ObjectBroker objectBroker;
	
	private InterceptorBroker interceptorBroker;

	private boolean osgiEnvironment = false;

	private static final Logger log = Logger.getLogger(IoTSySGateway.class.getName());

	private ObixServer obixServer = null;

	private MdnsResolver mdnsResolver;

	private Thread tomcat;
	private Thread tomcatNoSec;

	public IoTSySGateway() {

	}

	public void startGateway() {
		startGateway(DeviceLoader.DEVICE_CONFIGURATION_LOCATION);
	}

	public void startGateway(String devicesConfigFile) {

		Log.init();
		log.info("Server starting.");

		// init exi util
		ExiUtil.getInstance();

		// init contracts
		ContractInit.init();

		final String httpPort = PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.gateway.http.port", "8080");

		final String httpsPort = PropertiesLoader.getInstance().getProperties()
				.getProperty("iotsys.gateway.https.port", "8443");

		log.info("HTTP-Port: " + httpPort);

		// initialize object broker
		objectBroker = ObjectBrokerImpl.getInstance();
		obixServer = new ObixServerImpl(objectBroker);

		boolean enableServiceDiscovery = Boolean
				.parseBoolean(PropertiesLoader
						.getInstance()
						.getProperties()
						.getProperty("iotsys.gateway.servicediscovery.enabled",
								"false"));
		// set object broker to a shared global variable
		ObjectBrokerHelper.setInstance(objectBroker);

		if (enableServiceDiscovery) {
			objectBroker.setMdnsResolver(mdnsResolver);
		}

		// the following commented code block does not have any effect if running in OSGi environment
		// --> suggestion is to move to objectBroker
		
		// add initial objects to the database
//		if (devicesConfigFile == null)
//		{
//			deviceLoader = new DeviceLoaderImpl();
//		} else
//		{
//			deviceLoader = new DeviceLoaderImpl(devicesConfigFile);
//		}
//		connectors = deviceLoader.initDevices(objectBroker);
		// Transition step: migrate configs from devices.xml to DB, remove when done
//		ConfigsDbImpl.getInstance().migrate(connectors);
		
		if (!osgiEnvironment){
			objectBroker.initDevices(devicesConfigFile);
			// TODO: Re-apply written object from database, not work yet in osgi bundle
			// Be careful, this is causing some tests to fail due to object broker state is not fresh as 
			// there are objects fetched from DB
//			List<WritableObject> wos = WriteableObjectDbImpl.getInstance().getPersistedObjects();
//			for (WritableObject o : wos){
//				try {
//					obixServer.applyObj(new URI(o.getHref()), o.getDataStream());
//				} catch (URISyntaxException e) {
//					e.printStackTrace();
//				}
//			}
		}
		
		if (objectBroker.getMDnsResolver() != null)
		{
			log.info("No of records built: " + objectBroker.getMDnsResolver().getNumberOfRecord());
		} else
		{
			log.info("No MDNS resolver service found.");
		}

		interceptorBroker = InterceptorBrokerImpl.getInstance();
		// initialize interceptor broker
		boolean enableXacml = Boolean.parseBoolean(PropertiesLoader
				.getInstance().getProperties()
				.getProperty("iotsys.gateway.xacml", "false"));

		log.info("XACML module enabled: " + enableXacml);
		if (enableXacml && !isOsgiEnvironment()) {
			// temporarly register interceptor
			try {
				// load PDP interceptor if available on class path
				// load settings for remote pdp
				boolean remotePdp = Boolean
						.parseBoolean(PropertiesLoader
								.getInstance()
								.getProperties()
								.getProperty("iotsys.gateway.xacml.remotePDP",
										"false"));
				// PDPInterceptorSettings.getInstance().setRemotePdp(remotePdp);

				String remotePdpWsdl = PropertiesLoader.getInstance()
						.getProperties()
						.getProperty("iotsys.gateway.xacml.remotePDPWsdl", "");
				// PDPInterceptorSettings.getInstance().setRemotePdpWsdl(remotePdpWsdl);
				Class pdpSettingsClazz = null;

				try {
					pdpSettingsClazz = Class
							.forName("at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptorSettings");
					log.info("Class found: " + pdpSettingsClazz.getName());
				} catch (ClassNotFoundException e2)
				{
					e2.printStackTrace();
				}

				if (pdpSettingsClazz != null) {

					Method getInstance;
					try {
						getInstance = pdpSettingsClazz
								.getDeclaredMethod("getInstance");
						PDPInterceptorSettings settings = (PDPInterceptorSettings) getInstance
								.invoke(null, null);
						settings.setRemotePdpWsdl(remotePdpWsdl);
						settings.setRemotePdp(remotePdp);
					} catch (NoSuchMethodException e)
					{
						e.printStackTrace();
					} catch (SecurityException e)
					{
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						e.printStackTrace();
					} catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					} catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}

				}

				Class clazz = null;
				try
				{
					clazz = Class.forName("at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptor");
				} catch (ClassNotFoundException e1)
				{
					e1.printStackTrace();
				}

				if (clazz != null) {
					Interceptor interceptor;
					try {
						interceptor = (Interceptor) clazz.newInstance();
						interceptorBroker.register(interceptor);
					} catch (InstantiationException e)
					{
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						e.printStackTrace();
					}

				}
			} catch (ClassAlreadyRegisteredException e) {
				// silent exceptionhandling
			}
		}

		ObixObservingManager.getInstance().setObixServer(obixServer);

		new CoAPServer(obixServer);

		boolean enableSecurity = Boolean.parseBoolean(PropertiesLoader
				.getInstance().getProperties()
				.getProperty("iotsys.gateway.security.enable", "false"));

		// try {
		// if (enableSecurity) {
		// new TomcatServer(Integer.parseInt(httpsPort), obixServer);
		// } else {
		// new TomcatServerNoSecurity(Integer.parseInt(httpPort),
		// obixServer);
		// }
		// } catch (NumberFormatException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (ServletException e) {
		// e.printStackTrace();
		// }

		if (enableSecurity) {
			tomcat = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						new TomcatServer(Integer.parseInt(httpsPort),
								obixServer);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ServletException e) {
						e.printStackTrace();
					}
				}

			});
			tomcat.start();

		} else {
			tomcatNoSec = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						new TomcatServerNoSecurity(Integer.parseInt(httpPort),
								obixServer);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ServletException e) {
						e.printStackTrace();
					}
				}
			});
			tomcatNoSec.start();
		}

		// try
		// {
		// new NanoHTTPD(Integer.parseInt(httpPort), obixServer);
		// } catch (IOException ioe)
		// {
		// ioe.printStackTrace();
		// }
	}

	public void stopGateway() {
		objectBroker.shutdown();
		// CsvCreator.instance.close();
//		closeConnectors();

		if (tomcat != null)
			tomcat.interrupt();
		else if (tomcatNoSec != null)
			tomcatNoSec.interrupt();
	}

	public static void main(String[] args) {
		final IoTSySGateway iotsys = new IoTSySGateway();
		boolean enableServiceDiscovery = Boolean
				.parseBoolean(PropertiesLoader
						.getInstance()
						.getProperties()
						.getProperty("iotsys.gateway.servicediscovery.enabled",
								"false"));

		if (enableServiceDiscovery) {
			try {
				Named n = (Named) Class.forName(
						"at.ac.tuwien.auto.iotsys.mdnssd.NamedImpl")
						.newInstance();
				Class mc = Class
						.forName("at.ac.tuwien.auto.iotsys.mdnssd.MdnsResolverImpl");
				MdnsResolver m = (MdnsResolver) mc.getDeclaredMethod(
						"getInstance", null).invoke(null, null);

				n.startNamedService();
				iotsys.setMdnsResolver(m);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				log.info("Mdnssd service not found");
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}

		iotsys.startGateway();

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

	public boolean isOsgiEnvironment() {
		return osgiEnvironment;
	}

	public void setOsgiEnvironment(boolean osgiEnvironment) {
		this.osgiEnvironment = osgiEnvironment;
	}

	public ObjectBroker getObjectBroker() {
		return objectBroker;
	}

	public MdnsResolver getMdnsResolver() {
		return mdnsResolver;
	}

	public void setMdnsResolver(MdnsResolver mdnsResolver) {
		this.mdnsResolver = mdnsResolver;
		if (objectBroker != null)
			objectBroker.setMdnsResolver(mdnsResolver);
	}
}
