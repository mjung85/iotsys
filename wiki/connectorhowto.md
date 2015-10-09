

# How to create a technology connector #

## Project setup ##
Within the `iotsys` project directory, create a sub-project with the name of your connector, e.g. `iotsys-virtual`.

Use the following project layout:

```
iotsys-virtual/src - Java source files
iotsys-virtual/lib - External libraries, which are not available in a Maven repository
iotsys-virtual/test - Java source file for unit tests
iotsys-virtual/build.gradle
```

Create a gradle build file. Several grade tasks and settings are inherited from base build and settings file in the `iotsys` directory. In this build file, project specific tasks can be set. Also the project specific dependencies can be set here and the OSGI bundle specific attributes for the .jar Manifest file. The eclipse specific settings allow to automatically create the eclipse project file. Since gradle 1.4. has some issues with multi-project layouts, the dependencies to other iotsys-projects have to be set manually.

### Project dependencies ###
A connector depends at least on the following iotsys sub-projects:
```
iotsys-common
iotsys-obix
```
Add this dependencies in your IDE.

### Example gradle build file ###

`build.grade` for W-Bus connector:
```
// Variables
description = 'Virtual connector for the IoTSyS Gateway projects'

eclipse{
	classpath{
		file {
			withXml {
				def node = it.asNode()
				node.appendNode('classpathentry', [kind: 'src', path: '/iotsys-common', exported:'true'])
				node.appendNode('classpathentry', [kind: 'src', path: '/iotsys-obix', exported:'true'])
			}
		}
	}
}

configurations {
	provided
}

sourceSets{
	main { compileClasspath += configurations.provided }
}

jar {
	from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
	
	manifest {
		attributes("Manifest-Version": "1.0",
				   "Bundle-Name": "IoTSyS-Virtual",
				   "Bundle-Description": "Virtual connector for the IoTSyS Gateway project",
				   "Bundle-Vendor": "Vienna University of Technology, Automation Systems Group, www.auto.tuwien.ac.at",
				   "Bundle-Version": "0.1",
				   "Bundle-Classpath": ".",
				   "Bundle-Activator": "at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.VirtualBundleActivator",
				   "Import-Package": "obix, obix.asm, obix.contracts, obix.io, obix.net, obix.test, obix.toos, obix.ui, obix.ui.fields, obix.ui.views, obix.xml," +
									 "at.ac.tuwien.auto.iotsys.commons, at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot, at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators," +
									 "at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl, at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors," +
									 "at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl, org.osgi.framework," +
									 "javax.crypto, javax.crypto.interfaces, javax.crypto.spec," +
									 "org.xml.sax, org.xml.sax.helpers, org.xml.sax.ext,  com.sun.org.apache.xalan.internal, com.sun.org.apache.xalan.internal.res," +
									 "org.w3c.dom, org.w3c.dom.traversal, org.w3c.dom.ls, javax.xml.parsers,  javax.xml.xpath, javax.xml.transform.sax, javax.xml.transform.dom," +
									 "javax.xml.namespace, javax.xml.transform, javax.xml.transform.stream, javax.xml.validation, javax.xml.datatype," +
									 "org.apache.commons.beanutils, org.apache.commons.beanutils.converters,org.apache.commons.beanutils.expression, org.apache.commons.beanutils.locale,org.apache.commons.beanutils.locale.converters,org.apache.commons.collections, " +
									   "org.apache.commons.codec, org.apache.commons.codec.binary, org.apache.commons.codec.digest, org.apache.commons.codec.language, org.apache.commons.codec.language.bm, org.apache.commons.codec.net, " +
									   "org.apache.commons.collections.bag, org.apache.commons.collections.bidimap, org.apache.commons.collections.buffer, org.apache.commons.collections.collection, org.apache.commons.collections.comparators, org.apache.commons.collections.functors, org.apache.commons.collections.iterators, org.apache.commons.collections.keyvalue, org.apache.commons.collections.list, org.apache.commons.collections.map, org.apache.commons.collections.set, " +
									   "org.apache.commons.configuration, org.apache.commons.configuration.beanutils, org.apache.commons.configuration.event, org.apache.commons.configuration.interpol, org.apache.commons.configuration.plist, org.apache.commons.configuration.reloading, org.apache.commons.configuration.resolver, org.apache.commons.configuration.tree, org.apache.commons.configuration.tree.xpath, org.apache.commons.configuration.web, " +
									   "org.apache.commons.digester, org.apache.commons.digester.parser, org.apache.commons.digester.plugins, org.apache.commons.digester.plugins.strategies, org.apache.commons.digester.substitution, org.apache.commons.digester.xmlrules, " +
									   "org.apache.commons.jexl2, org.apache.commons.jexl2.internal, org.apache.commons.jexl2.internal.introspection, org.apache.commons.jexl2.introspection, org.apache.commons.jexl2.parser, org.apache.commons.jexl2.scripting, " +
									   "org.apache.commons.jxpath, org.apache.commons.jxpath.functions, org.apache.commons.jxpath.ri, org.apache.commons.jxpath.ri.axes, org.apache.commons.jxpath.ri.compiler, org.apache.commons.jxpath.ri.model, org.apache.commons.jxpath.ri.model.beans, org.apache.commons.jxpath.ri.model.container, org.apache.commons.jxpath.ri.model.dom, org.apache.commons.jxpath.ri.model.dynabeans, org.apache.commons.jxpath.ri.model.dynamic, org.apache.commons.jxpath.ri.model.jdom, org.apache.commons.jxpath.ri.parser,org.apache.commons.jxpath.servlet, org.apache.commons.jxpath.util, org.apache.commons.jxpath.xml, " +
									   "org.apache.commons.lang, org.apache.commons.lang.builder, org.apache.commons.lang.enums, org.apache.commons.lang.exception, org.apache.commons.lang.math, org.apache.commons.lang.mutable, org.apache.commons.lang.text, org.apache.commons.lang.time, " +
									   "org.apache.commons.logging, org.apache.commons.logging.impl"
			 
				)
	}
}

dependencies {
	provided project(':iotsys-obix')
	provided project(':iotsys-common')
	
	//provided group: 'org.apache.felix', name: 'org.apache.felix.framework', version: '4.2.0'
	provided group: 'org.apache.felix', name: 'org.osgi.core', version: '1.4.0'
	
	provided group: 'commons-beanutils', name: 'commons-beanutils', version: '1.8.3'
	provided group: 'commons-codec', name: 'commons-codec', version: '1.6'
	provided group: 'commons-collections', name: 'commons-collections', version: '3.2.1'
	provided group: 'commons-configuration', name: 'commons-configuration', version: '1.9'
	provided group: 'commons-digester', name: 'commons-digester', version: '1.8.1'
	
	
	provided group: 'org.apache.commons', name: 'commons-jexl', version: '2.1.1'
	provided group: 'commons-jxpath', name: 'commons-jxpath', version: '1.3'
	provided group: 'commons-lang', name: 'commons-lang', version: '2.3'
	provided group: 'commons-logging', name: 'commons-logging', version: '1.1.1'
}
```

Modify `settings.gradle` in the `iotsys` directory, and add your project to be included in the multi-project layout.

```
include 'iotsys-obix', 'iotsys-common', 'iotsys-encoding-json', 'iotsys-gateway', 'iotsys-calimero', 'iotsys-knx', 'iotsys-bacnet4j', 'iotsys-bacnet', 'iotsys-wmbus', 'iotsys-virtual'
```

Now you can use the gradle tasks to build and deploy your project:

```
gradle clean
gradle compileJava
gradle deployOsgi
gradle eclipse
```

After executing `gradle eclipse` you can import the project into eclipse. If you want to use the connector within eclipse together with the gateway add the new connector as project dependency for the gateway, in order to make it available at runtime. There should be no compile time dependency to a connector but at runtime the classes need to be available in the JVM.

## Create a technology connector ##
The technology connector is responsible for connecting to your specific automation technology. It should establish connection to local bus interfaces and allow the gateway to connect or disconnect from it.

For the interaction between the devices and the gateway all interfaces are provided in the iotsys-common project.

The connector should also include the API used to interact with the bus. For example to read and write datapoints, since the connector will be passed to each technology specific oBIX object implementation.

### Connector example ###
```
package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;

public class VirtualConnector implements Connector {
	private static final Logger log = Logger.getLogger(VirtualConnector.class
			.getName());

	@Override
	public void connect() throws Exception {
		log.info("VirtualConnector connecting.");

	}

	@Override
	public void disconnect() throws Exception {
		log.info("VirtualConnector disconnecting.");
	}

	public Boolean readBoolean(Object busAddress) {
		return true;
	}

	public void writeBoolean(Object busAddress, Boolean value) {

	}

	public Double readDouble(Object busAddress) {
		return 0.0;
	}

	public void writeDouble(Object busAddress, Double value) {

	}
}

```

### Create technology specific devices ###
A technology specific implementation of the base oBIX objects found in the common project adds a constructor with the technology connector and bus specific addressing information.

Virtual `TemperatureSensor`:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.virtual;

import java.util.logging.Logger;

import obix.Obj;

import at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.VirtualConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;

public class TemperatureSensorImplVirtual extends TemperatureSensorImpl {
	private static final Logger log = Logger.getLogger(TemperatureSensorImplVirtual.class.getName());
	
	private VirtualConnector virtualConnector;
	private Object busAddress; // dummy Object, modify it according to your technology
	
	// Add further construtor parameters for bus address information for this temperature sensor
	public TemperatureSensorImplVirtual(VirtualConnector virtualConnector, Object busAddress){
		// technology specific initialization
		this.virtualConnector = virtualConnector;
		this.busAddress = busAddress;
	}
	
	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
	}
	
	@Override
	public void writeObject(Obj input){
		// It is not possible to write on a sensor
	}
	
	@Override
	public void refreshObject(){
		// value is the protected instance variable of the base class (TemperatureSensorImpl)
		if(value != null){
			Double value = virtualConnector.readDouble(busAddress);
			
			// this calls the implementation of the base class, which triggers als
			// oBIX services (e.g. watches, history) and CoAP observe!
			
			this.value().set(value); 
		}	
	}
}
```

Virtual `LightSwitchActuator`:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.object.iot.actuators.impl;

import java.util.logging.Logger;

import obix.Obj;

import at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.VirtualConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.LightSwitchActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.virtual.TemperatureSensorImplVirtual;

public class LightSwitchActuatorImplVirtual extends LightSwitchActuatorImpl {
private static final Logger log = Logger.getLogger(TemperatureSensorImplVirtual.class.getName());
	
	private VirtualConnector virtualConnector;
	private Object busAddress; // dummy Object, modify it according to your technology
	
	// Add further constructor parameters for bus address information for this temperature sensor
	public LightSwitchActuatorImplVirtual(VirtualConnector virtualConnector, Object busAddress){
		// technology specific initialization
		this.virtualConnector = virtualConnector;
		this.busAddress = busAddress;
	}
	
	
	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
	}
	
	@Override
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		// The base class knows how to update the internal variable and to trigger
		// all the oBIX specific processing.
		super.writeObject(input);
		
		// write it out to the technology bus
		virtualConnector.writeBoolean(busAddress, this.value().get());	
	}
	
	@Override
	public void refreshObject(){
		// value is the protected instance variable of the base class (TemperatureSensorImpl)
		if(value != null){
			Boolean value = virtualConnector.readBoolean(busAddress);	
			
			// this calls the implementation of the base class, which triggers als
			// oBIX services (e.g. watches, history) and CoAP observe!
			
			this.value().set(value); 
		}	
	}
}

```

### Create device loader ###
Now it is time to let the gateway use the technology connector. Therefore, a device loader is required that is responsible for making the technology available to the gateway. This includes the instantiation of one or multiple connectors for this technology and the instantation of the technology specific objects. The device loader has to implement the `DeviceLoader` interface provided by the iotsys-common project. The `ObjectBroker` is used to register objects and allows to configure IPv6 alias addresses and to enable a generic history mechanism for basic data points.

The device loader can either connect to the bus and instantiate device objects hard-coded or use an XML config. The code example below illustrates both approaches.

`VirutalDeviceLoader`:
```
package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import obix.Obj;
import obix.Uri;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;
import at.ac.tuwien.auto.iotsys.gateway.obix.object.iot.actuators.impl.LightSwitchActuatorImplVirtual;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.LightSwitchActuatorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.virtual.TemperatureSensorImplVirtual;

public class VirtualDeviceLoaderImpl implements DeviceLoader {
	private final ArrayList<String> myObjects = new ArrayList<String>();

	private XMLConfiguration devicesConfig = new XMLConfiguration();

	private static final Logger log = Logger
			.getLogger(VirtualDeviceLoaderImpl.class.getName());

	public VirtualDeviceLoaderImpl() {
		String devicesConfigFile = DEVICE_CONFIGURATION_LOCATION;

		try {
			devicesConfig = new XMLConfiguration(devicesConfigFile);
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public ArrayList<Connector> initDevices(ObjectBroker objectBroker) {
		// Hard-coded connections and object creation

		// store all created connectors, will be used by the gateway for closing
		ArrayList<Connector> connectors = new ArrayList<Connector>();
		// Open connection
		VirtualConnector virtualConnector = new VirtualConnector();
		try {
			virtualConnector.connect();

			connectors.add(virtualConnector);

			// add virtual devices

			TemperatureSensorImpl virtualTemp1 = new TemperatureSensorImplVirtual(
					virtualConnector, new Object());
			virtualTemp1.setHref(new Uri("virtualTemp1"));
			virtualTemp1.setName("virtualTemp1");

			// add virtual devices to object broker and remember all assigned
			// URIs, due to child objects there could be one or many
			synchronized (myObjects) {
				myObjects.addAll(objectBroker.addObj(virtualTemp1));
			}

			// add obj with IPv6 address
			// String ipv6 = "fe80::1"
			// myObjects.addAll(objectBroker.addObj(virtualTemp1, ipv6));

			// enable history yes/no?
			objectBroker.addHistoryToDatapoints(virtualTemp1, 100);

			LightSwitchActuatorImpl virtualLight1 = new LightSwitchActuatorImplVirtual(
					virtualConnector, new Object());
			virtualLight1.setHref(new Uri("virtualLight1"));
			virtualLight1.setName("virtualLight1");

			// add virtual devices to object broker
			objectBroker.addObj(virtualLight1);

			// add obj with IPv6 address
			// String ipv6 = "fe80::1"
			// objectBroker.addObj(virtualTemp1, ipv6);

			// enable history yes/no?
			objectBroker.addHistoryToDatapoints(virtualLight1, 100);

		} catch (Exception e) {

			e.printStackTrace();
		}

		// parse XML configuration for connections and objects
		// NOTE: this loader allow to directly instantiate the base oBIX objects
		// for testing purposes
		int connectorsSize = 0;
		// virtual
		Object virtualConnectors = devicesConfig
				.getProperty("virtual.connector.name");
		if (virtualConnectors != null) {
			connectorsSize = 1;
		} else {
			connectorsSize = 0;
		}

		if (virtualConnectors instanceof Collection<?>) {
			virtualConnectors = ((Collection<?>) virtualConnectors).size();
		}

		for (int connector = 0; connector < connectorsSize; connector++) {
			HierarchicalConfiguration subConfig = devicesConfig
					.configurationAt("virtual.connector(" + connector + ")");

			Object virtualConfiguredDevices = subConfig
					.getProperty("device.type");
			String connectorName = subConfig.getString("name");
			Boolean enabled = subConfig.getBoolean("enabled", false);

			if (enabled) {
				try {
					if (virtualConfiguredDevices instanceof Collection<?>) {
						Collection<?> wmbusDevice = (Collection<?>) virtualConfiguredDevices;
						log.info(wmbusDevice.size()
								+ " virtual devices found in configuration for connector "
								+ connectorName);

						for (int i = 0; i < wmbusDevice.size(); i++) {
							String type = subConfig.getString("device(" + i
									+ ").type");
							List<Object> address = subConfig.getList("device("
									+ i + ").address");
							String ipv6 = subConfig.getString("device(" + i
									+ ").ipv6");
							String href = subConfig.getString("device(" + i
									+ ").href");

							Boolean historyEnabled = subConfig.getBoolean(
									"device(" + i + ").historyEnabled", false);

							Integer historyCount = subConfig.getInt("device("
									+ i + ").historyCount", 0);

							if (type != null && address != null) {
								try {

									Obj virtualObj = (Obj) Class.forName(type)
											.newInstance();
									virtualObj.setHref(new Uri(href));

									if (ipv6 != null) {
										myObjects.addAll(objectBroker.addObj(virtualObj, ipv6));
									} else {
										myObjects.addAll(objectBroker.addObj(virtualObj));
									}

									virtualObj.initialize();

									if (historyEnabled != null
											&& historyEnabled) {
										if (historyCount != null
												&& historyCount != 0) {
											objectBroker
													.addHistoryToDatapoints(
															virtualObj,
															historyCount);
										} else {
											objectBroker
													.addHistoryToDatapoints(virtualObj);
										}
									}

								} catch (SecurityException e) {
									e.printStackTrace();
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
						}
					} else {
						log.info("No virtual devices configured for connector "
								+ connectorName);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return connectors;
	}

	@Override
	public void removeDevices(ObjectBroker objectBroker) {
		synchronized (myObjects) {
			for (String href : myObjects) {
				objectBroker.removeObj(href);
			}
		}

	}
}

```

### Configure the device loader ###
If the gateway is used standalone, e.g. started within Eclipse, it uses the devices.xml file to find technology specifc device loaders.

Adjust the `iotsys/iotsys-gateway/config/devices.xml` and add your device loader.

Modified devices.xml:
```
<deviceloaders>
    <device-loader>at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXDeviceLoaderImpl
    </device-loader>
    <device-loader>at.ac.tuwien.auto.iotsys.gateway.connectors.bacnet.BacnetDeviceLoaderImpl
    </device-loader>
    <device-loader>at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.WMBusDeviceLoaderImpl
    </device-loader>
    <device-loader>at.ac.tuwien.auto.iotsys.gateway.connectors.virtual.VirtualDeviceLoaderImpl
    </device-loader>
  </deviceloaders>
```

Now you can test your technology connector already in the Eclipse standalone setup. Execute the main method of the `IoTSySGateway` class, if you have added it as project dependency to the gateway project. Otherwise the device loader of the gateway will not find your device loader.

## OSGI bundle activator ##
For a standalone deployment the steps above are enough. If you want to use the technology connector within OSGI you have to provide a bundle activator. Within the iotsys-gateway project the technology bundle should have a life-cycle in which it connects to technology on startup of the bundle and disconnects if it is stopped. Furthermore, it should lookup the `ObjectBroker` of the gateway bundle and register its objects. The example below illustrates the process. Note that in an OSGI environment the configured device loaders in the devices.xml have no effect. The device loaders are triggered through the bundle activator by itself.

```
package at.ac.tuwien.auto.iotsys.gateway.connectors.virtual;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.commons.DeviceLoader;
import at.ac.tuwien.auto.iotsys.commons.ObjectBroker;


public class VirtualBundleActivator implements ServiceListener, BundleActivator {
	private static final Logger log = Logger.getLogger(VirtualBundleActivator.class
			.getName());

	private DeviceLoader deviceLoader = new VirtualDeviceLoaderImpl();
	private ArrayList<Connector> connectors = null;

	private volatile boolean registered = false;

	private BundleContext context = null;

	public void start(BundleContext context) throws Exception {
		log.info("Starting Virtual connector");
		this.context = context;
		ServiceReference<ObjectBroker> serviceReference = context
				.getServiceReference(ObjectBroker.class);
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to register devices!");

		} else {
			synchronized (this) {
				log.info("Initiating Virtual devices.");
				ObjectBroker objectBroker = (ObjectBroker) context
						.getService(serviceReference);
				connectors = deviceLoader.initDevices(objectBroker);
				registered = true;
			}

		}

		context.addServiceListener(this);
	}

	public void stop(BundleContext context) throws Exception {
		log.info("Stopping virtual connector");
		ServiceReference<ObjectBroker> serviceReference = context
				.getServiceReference(ObjectBroker.class);
		if (serviceReference == null) {
			log.severe("Could not find a running object broker to unregister devices!");
		} else {
			log.info("Removing virtual devices.");
			ObjectBroker objectBroker = (ObjectBroker) context
					.getService(serviceReference);
			deviceLoader.removeDevices(objectBroker);
			if (connectors != null) {
				for (Connector connector : connectors) {
					try {
						connector.disconnect();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[]) event.getServiceReference()
				.getProperty("objectClass");

		if (event.getType() == ServiceEvent.REGISTERED) {
			if (objectClass[0].equals(ObjectBroker.class.getName())) {

				synchronized (this) {
					log.info("ObjectBroker detected.");

					if (!registered) {
						ObjectBroker objectBroker = (ObjectBroker) context
								.getService(event.getServiceReference());
						try {
							connectors = deviceLoader.initDevices(objectBroker);
							registered = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

		} 
	}
}
```

Now you can test your bundle in the OSGI environment, by running the commands below:

```
iotsys# gradle clearOsgiCache
iotsys# gradle deployOsgi
iotsys/felix-framework-4.2.0# java -jar bin/felix.jar
```