## oBIX IoT contracts ##

IoTSyS aims providing a generic set of domain specific contracts for sensor and actuator systems found in nowadays home and building automation systems. The approach is inspired by the KNX domain specific (e.g. lighting, HVAC, security, ...) functional blocks that define which input and output datapoints a certain device implementing this function block has to offer.

This is represented by the oBIX objects and contracts found in the iotsys-common project.

### Example ###

Some example contracts:

```
<obj href="iot:LightSwitchActuator">
  <bool name="value" href="value" val="false" writable="true"/>
</obj>
```

```
<obj href="iot:TemperatureSensor">
  <real name="value" href="value" val="28.21600341796875" unit="obix:units/celsius"/>
</obj>
```

## oBIX Java classes ##
The Java classes used to represent these contracts use the oBIX Toolkit. For each contract a interface and an implementation class needs to be provided. The interface represents the contract and needs to be registered at the `ContractRegistry` of the oBIX Toolkit in order to be able to serialize Java oBIX objects to XML and to deserialize XML to Java oBIX objects. The interfaces and implementation of the standard actuator and sensor types can be found with the `at.ac.tuwien.auto.iotsys.gateway.obix.object.iot` sub-packages.

The interface registration is currently done within the iotsys-gateway project in the `at.ac.tuwien.auto.iotsys.gateway.obix.objects.ContractInit` class.

Feel free to create new device types but don't forget to register the interfaces at the `ContractRegistry` otherwise the gateway cannot handle it.

The base implementation of the oBIX object can be used within the oBIX server and takes care about all oBIX specific features (history, watches, alarming) and can already be used to interact with the oBIX server (e.g. the gateway) using the different protocol bindings. Technology connectors should subclass this base implementation and should only take care to interact with the according bus. However, the technology specific implementation is encapsulated in a separate module (OSGI bundle) and the gateway has no dependency to the technology specific implementations.

### Examples ###

`LightSwitch` contract:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators;

import obix.Bool;

public interface LightSwitchActuator extends Actuator {
	public static final String CONTRACT="iot:LightSwitchActuator";
	
	public static final String VALUE_CONTRACT_NAME = "value";
	public static final String VALUE_CONTRACT_HERF = "value";
	
	public static final String switchContract = "<bool name='" + VALUE_CONTRACT_NAME + "' href='" + VALUE_CONTRACT_HERF + "' val='false'/>";
	public Bool value();
}
```



`LightSwitchActuator` base implementation:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.LightSwitchActuator;
import obix.Bool;
import obix.Contract;
import obix.Obj;
import obix.Uri;

public class LightSwitchActuatorImpl extends ActuatorImpl implements LightSwitchActuator{
	protected Bool value = new Bool(false);
	
	public LightSwitchActuatorImpl(){
		setIs(new Contract(LightSwitchActuator.CONTRACT));
		value.setWritable(true);
		Uri valueUri = new Uri(LightSwitchActuator.VALUE_CONTRACT_HERF);
	
		value.setHref(valueUri);
		value.setName(LightSwitchActuator.VALUE_CONTRACT_NAME);			
		
		add(value);
	}
	
	public void writeObject(Obj input){
		
		// A write on this object was received, update the according data point.		
		boolean newVal = false;
		
		if(input instanceof LightSwitchActuator){
			LightSwitchActuator in = (LightSwitchActuator) input;
			newVal = in.value().get();
			
		}
		else if(input instanceof Bool){
			newVal = ((Bool) input).get();
		}
		this.value.set(newVal);
	}

	public Bool value() {
		return this.value;
	}	
}	
```

`LightSwitchActuator` technology (KNX) specific implementation:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.knx;

import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlatorBoolean;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.impl.LightSwitchActuatorImpl;
import obix.Obj;

/**
 * Provides the KNX specific implementation for a light switching actuator.
 */
public class LightSwitchActuatorImplKnx extends LightSwitchActuatorImpl  {
	private GroupAddress status;
	private GroupAddress switching;
	private KNXConnector knxConnector;
	
	public static final Logger knxBus = KNXConnector.knxBus;
	
	public LightSwitchActuatorImplKnx(KNXConnector knxConnector, GroupAddress status, final GroupAddress switching){
		super();
		this.status = status;
		this.switching = switching;
		this.knxConnector = knxConnector;
		if(status == null){
			// add watchdog on switching group address
			knxConnector.addWatchDog(switching, new KNXWatchDog() {
				@Override
				public void notifyWatchDog(byte[] apdu) {			
					try {
						DPTXlatorBoolean x = new DPTXlatorBoolean(DPTXlatorBoolean.DPT_SWITCH);
											
						x.setData(apdu);

						if(x.getValueBoolean() != LightSwitchActuatorImplKnx.this.value.get()){
							LightSwitchActuatorImplKnx.this.value.set(x.getValueBoolean());
						}
						
					} catch (KNXException e) {					
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void writeObject(Obj input){
		// A write on this object was received, update the according data point.	
		super.writeObject(input);
	
		knxConnector.write(switching, this.value().get());	
	}
	
	public void refreshObject(){
		if(status != null){
			boolean value = knxConnector.readBool(status);		
			this.value().set(value);
		}	
	}
}
```

`TemperatureSensor` contract:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors;

import obix.Real;

public interface TemperatureSensor extends Sensor{
	
	public static final String CONTRACT="iot:TemperatureSensor";
	
	public static final String valueContract = "<real name='value' href='value' val='0'/>";
	public Real value();
}

```

`TemperatureSensor` base implementation:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl;

import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.TemperatureSensor;
import obix.Real;
import obix.Contract;
import obix.Obj;
import obix.Uri;

public class TemperatureSensorImpl extends SensorImpl implements
		TemperatureSensor {
	protected Real value = new Real(0);	

	public TemperatureSensorImpl() {
		setIs(new Contract(TemperatureSensor.CONTRACT));
		value.setWritable(false);
		Uri valueUri = new Uri("value");
		
		value.setHref(valueUri);
		value.setName("value");
		value.setUnit(new Uri("obix:units/celsius"));
		add(value);				
	}

	public void writeObject(Obj input) {
		// not writable
	}

	public Real value() {
		return this.value;
	}
	
	@Override
	public void initialize(){
		super.initialize();	
	}
}
```

`TemperatureSensor` technology (KNX) specific implementation:
```
package at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.knx;

import java.util.logging.Logger;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator2ByteFloat;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXWatchDog;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.impl.TemperatureSensorImpl;

public class TemperatureSensorImplKnx extends TemperatureSensorImpl{
	
	private static Logger log = Logger.getLogger(TemperatureSensorImplKnx.class.getName());
	
	private GroupAddress observation;
	private KNXConnector connector;
	
	public TemperatureSensorImplKnx(KNXConnector connector , GroupAddress observation) {
		this.observation = observation;
		this.connector = connector;	
	}
	
	public void createWatchDog() {		
		
		connector.addWatchDog(observation, new KNXWatchDog() {
			@Override
			public void notifyWatchDog(byte[] apdu) {			
				try {					
					DPTXlator2ByteFloat x = new DPTXlator2ByteFloat(DPTXlator2ByteFloat.DPT_TEMPERATURE);					
					
					x.setData(apdu, 0);
														
					String[] a = x.getAllValues();										
								
					log.fine("Temperature for " + TemperatureSensorImplKnx.this.getHref() + " now " + x.getValueFloat(1));
					value.set(x.getValueFloat(1));									
				} 
				catch (KNXException e){				
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void initialize(){
		super.initialize();
		createWatchDog();	
	}
}
```