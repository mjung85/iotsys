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
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_5_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_9_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPST_9_8;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPT_1;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPT_3;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPT_5_A;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.DPT_9;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.datapoint.Datapoint;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.encoding.EncodingOnOff;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.entity.Entities;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.entity.Entity;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumConnector;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumEnabled;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumLanguage;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumPart;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumPriority;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumStandard;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.enumeration.EnumTranslation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.language.Multilingual;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.language.Translation;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.network.Network;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.parameter.ParameterDimming;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.unit.UnitCelsius;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.unit.UnitPercent;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.unit.UnitPpm;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Area;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Domain;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Group;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.Part;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.ViewBuilding;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.ViewDomains;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.ViewFunctional;
import at.ac.tuwien.auto.iotsys.gateway.obix.objects.general.view.ViewTopology;
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

		// Sensor
		ContractRegistry.put(ShuttersAndBlindsSunblindSensor.CONTRACT, ShuttersAndBlindsSunblindSensor.class.getName());
		ContractRegistry.put(PresenceDetectorSensor.CONTRACT, PresenceDetectorSensor.class.getName());
		ContractRegistry.put(RoomRelativeHumiditySensor.CONTRACT, RoomRelativeHumiditySensor.class.getName());
		ContractRegistry.put(SunIntensitySensor.CONTRACT, SunIntensitySensor.class.getName());
		ContractRegistry.put(SmartMeter.CONTRACT, SmartMeter.class.getName());
		ContractRegistry.put(IndoorBrightnessSensor.CONTRACT, IndoorBrightnessSensor.class.getName());
		ContractRegistry.put(OutsideTemperatureSensor.CONTRACT, OutsideTemperatureSensor.class.getName());

		// Actuator
		ContractRegistry.put(FanSpeedActuator.CONTRACT, FanSpeedActuator.class.getName());
		ContractRegistry.put(HVACvalveActuator.CONTRACT, HVACvalveActuator.class.getName());
		ContractRegistry.put(AirDamperActuator.CONTRACT, AirDamperActuator.class.getName());
		ContractRegistry.put(BoilerActuator.CONTRACT, BoilerActuator.class.getName());
		ContractRegistry.put(CoolerActuator.CONTRACT, CoolerActuator.class.getName());
		ContractRegistry.put(PumpActuator.CONTRACT, PumpActuator.class.getName());
		ContractRegistry.put(SunblindActuator.CONTRACT, SunblindActuator.class.getName());

		ContractRegistry.put(GroupComm.CONTRACT, GroupComm.class.getName());
		// ContractRegistry.put(TemperatureControlActuator.CONTRACT,
		// TemperatureControlActuator.class.getName());
		// ContractRegistry.put(ChillerActuator.CONTRACT,
		// ChillerActuator.class.getName());
		// ContractRegistry.put(HeatPumpActuator.CONTRACT,
		// HeatPumpActuator.class.getName());

		// Generic Bacnet
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

		// Gateway Commons
		ContractRegistry.put(Datapoint.CONTRACT, Datapoint.class.getName());
		ContractRegistry.put(DPT_1.CONTRACT, DPT_1.class.getName());
		ContractRegistry.put(DPT_3.CONTRACT, DPT_3.class.getName());
		ContractRegistry.put(DPT_5_A.CONTRACT, DPT_5_A.class.getName());
		ContractRegistry.put(DPT_9.CONTRACT, DPT_9.class.getName());
		ContractRegistry.put(DPST_1_1.CONTRACT, DPST_1_1.class.getName());
		ContractRegistry.put(DPST_3_7.CONTRACT, DPST_3_7.class.getName());
		ContractRegistry.put(DPST_5_1.CONTRACT, DPST_5_1.class.getName());
		ContractRegistry.put(DPST_9_1.CONTRACT, DPST_9_1.class.getName());
		ContractRegistry.put(DPST_9_8.CONTRACT, DPST_9_8.class.getName());

		ContractRegistry.put(Entities.CONTRACT, Entities.class.getName());
		ContractRegistry.put(Entity.CONTRACT, Entity.class.getName());

		ContractRegistry.put(EnumConnector.CONTRACT, EnumConnector.class.getName());
		ContractRegistry.put(EnumEnabled.CONTRACT, EnumEnabled.class.getName());
		ContractRegistry.put(EnumLanguage.CONTRACT, EnumLanguage.class.getName());
		ContractRegistry.put(EnumPart.CONTRACT, EnumPart.class.getName());
		ContractRegistry.put(EnumPriority.CONTRACT, EnumPriority.class.getName());
		ContractRegistry.put(EnumStandard.CONTRACT, EnumStandard.class.getName());
		ContractRegistry.put(EnumTranslation.CONTRACT, EnumTranslation.class.getName());

		ContractRegistry.put(EncodingOnOff.CONTRACT, EncodingOnOff.class.getName());

		ContractRegistry.put(Multilingual.CONTRACT, Multilingual.class.getName());
		ContractRegistry.put(Translation.CONTRACT, Translation.class.getName());

		ContractRegistry.put(Network.CONTRACT, Network.class.getName());

		ContractRegistry.put(ParameterDimming.CONTRACT, ParameterDimming.class.getName());

		ContractRegistry.put(UnitCelsius.CONTRACT, UnitCelsius.class.getName());
		ContractRegistry.put(UnitPercent.CONTRACT, UnitPercent.class.getName());
		ContractRegistry.put(UnitPpm.CONTRACT, UnitPpm.class.getName());

		ContractRegistry.put(Area.CONTRACT, Area.class.getName());
		ContractRegistry.put(Domain.CONTRACT, Domain.class.getName());
		ContractRegistry.put(Group.CONTRACT, Group.class.getName());
		ContractRegistry.put(Part.CONTRACT, Part.class.getName());

		ContractRegistry.put(ViewBuilding.CONTRACT, ViewBuilding.class.getName());
		ContractRegistry.put(ViewDomains.CONTRACT, ViewDomains.class.getName());
		ContractRegistry.put(ViewFunctional.CONTRACT, ViewFunctional.class.getName());
		ContractRegistry.put(ViewTopology.CONTRACT, ViewTopology.class.getName());

		ContractRegistry.buildReverseMap();
	}

}
