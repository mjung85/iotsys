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
package at.ac.tuwien.auto.iotsys.gateway.obix.objects;

import obix.ContractRegistry;
import at.ac.tuwien.auto.iotsys.gateway.obix.groupcomm.GroupComm;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.AnalogInput;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.AnalogOutput;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.AnalogValue;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryInput;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryOutput;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.bacnet.BinaryValue;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_1_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_3_7;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_9_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DataPoint;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.AirDamperActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.BoilerActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.BrightnessActuator;
//import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.ChillerActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.CoolerActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.FanSpeedActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.HVACvalveActuator;
//import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.HeatPumpActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.LightSwitchActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.PumpActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.SunblindActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.BinaryOperation;
//import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.actuators.TemperatureControlActuator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.Comparator;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.LogicBinaryOperation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.logic.TemperatureController;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.IndoorBrightnessSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.OutsideTemperatureSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.PresenceDetectorSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.PushButton;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.RoomRelativeHumiditySensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.ShuttersAndBlindsSunblindSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.SmartMeter;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.SunIntensitySensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sensors.TemperatureSensor;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.iot.sim.HVACSimulation;

public class ContractInit
{

  public static void init()
  {
	  ContractRegistry.put(LightSwitchActuator.CONTRACT, LightSwitchActuator.class.getName());
	  ContractRegistry.put(BrightnessActuator.CONTRACT, BrightnessActuator.class.getName());
	  ContractRegistry.put(PushButton.CONTRACT, PushButton.class.getName());
	  ContractRegistry.put(TemperatureSensor.CONTRACT, TemperatureSensor.class.getName());
	  
	  //Sensor
	  ContractRegistry.put(ShuttersAndBlindsSunblindSensor.CONTRACT,ShuttersAndBlindsSunblindSensor.class.getName());
	  ContractRegistry.put(PresenceDetectorSensor.CONTRACT,PresenceDetectorSensor.class.getName());
	  ContractRegistry.put(RoomRelativeHumiditySensor.CONTRACT,RoomRelativeHumiditySensor.class.getName());
	  ContractRegistry.put(SunIntensitySensor.CONTRACT,SunIntensitySensor.class.getName());
	  ContractRegistry.put(SmartMeter.CONTRACT, SmartMeter.class.getName());
	  ContractRegistry.put(IndoorBrightnessSensor.CONTRACT, IndoorBrightnessSensor.class.getName());
	  ContractRegistry.put(OutsideTemperatureSensor.CONTRACT, OutsideTemperatureSensor.class.getName());
	  
	  //Actuator
	  ContractRegistry.put(FanSpeedActuator.CONTRACT,FanSpeedActuator.class.getName());
	  ContractRegistry.put(HVACvalveActuator.CONTRACT,HVACvalveActuator.class.getName());
	  ContractRegistry.put(AirDamperActuator.CONTRACT,AirDamperActuator.class.getName());
	  ContractRegistry.put(BoilerActuator.CONTRACT, BoilerActuator.class.getName());
	  ContractRegistry.put(CoolerActuator.CONTRACT, CoolerActuator.class.getName());
	  ContractRegistry.put(PumpActuator.CONTRACT, PumpActuator.class.getName());
	  ContractRegistry.put(SunblindActuator.CONTRACT, SunblindActuator.class.getName());
	  
	  ContractRegistry.put(GroupComm.CONTRACT, GroupComm.class.getName());
//	  ContractRegistry.put(TemperatureControlActuator.CONTRACT, TemperatureControlActuator.class.getName());
//	  ContractRegistry.put(ChillerActuator.CONTRACT, ChillerActuator.class.getName());
//	  ContractRegistry.put(HeatPumpActuator.CONTRACT, HeatPumpActuator.class.getName());
	  
	  //Generic Bacnet
	  ContractRegistry.put(AnalogInput.CONTRACT, AnalogInput.class.getName());
	  ContractRegistry.put(AnalogOutput.CONTRACT, AnalogOutput.class.getName());
	  ContractRegistry.put(AnalogValue.CONTRACT, AnalogValue.class.getName());
	  ContractRegistry.put(BinaryInput.CONTRACT, BinaryInput.class.getName());
	  ContractRegistry.put(BinaryOutput.CONTRACT, BinaryOutput.class.getName());
	  ContractRegistry.put(BinaryValue.CONTRACT, BinaryValue.class.getName());
	  
	  // Logic components
	  ContractRegistry.put(Comparator.CONTRACT, Comparator.class.getName());
	  ContractRegistry.put(TemperatureController.CONTRACT, TemperatureController.class.getName());
	  ContractRegistry.put(BinaryOperation.CONTRACT, BinaryOperation.class.getName());
	  ContractRegistry.put(LogicBinaryOperation.CONTRACT, LogicBinaryOperation.class.getName());
	  
	  // Simulation
	  ContractRegistry.put(HVACSimulation.CONTRACT, HVACSimulation.class.getName());
	  
	  
	  // KNX 
	  ContractRegistry.put(DataPoint.CONTRACT, DataPoint.class.getName());
	  ContractRegistry.put(DPST_9_1.CONTRACT, DPST_9_1.class.getName());
	  ContractRegistry.put(DPST_1_1.CONTRACT, DPST_1_1.class.getName());
	  ContractRegistry.put(DPST_3_7.CONTRACT, DPST_3_7.class.getName());
	 
	  ContractRegistry.buildReverseMap();
  }

}
