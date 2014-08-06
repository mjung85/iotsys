/*******************************************************************************
 * Copyright (c) 2014
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

package at.ac.tuwien.auto.iotsys.gateway.obix.objects.enocean.entity.impl;

import java.util.logging.Logger;

import org.opencean.core.ESP3Host;
import org.opencean.core.EnoceanWatchdog;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket4BS;
import org.opencean.core.utils.Bits;
import org.opencean.core.utils.ByteBitSet;

import obix.Bool;
import obix.Int;
import obix.Obj;
import obix.Real;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTBoolOnOffImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTIntPercImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTRealTempImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.EntityEEP_F60201;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.impl.EnoceanEntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.EncodingPressedReleased;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.impl.EncodingsImpl;

public class EntityEEP_A52001Impl extends EnoceanEntityImpl implements EntityEEP_F60201
{	
	private static Logger log = Logger.getLogger(EntityEEP_A52001Impl.class.getName());
		
	protected final ESP3Host esp3Host;	
	protected final EnoceanId id;			
	EnoceanDPTIntPercImpl datapoint_percent;
	EnoceanDPTRealTempImpl datapoint_temp;
	EnoceanDPTRealTempImpl datapoint_currenttemp;
	EnoceanDPTBoolOnOffImpl datapoint_setpoint; // off = valve pos (0-100), on = temp (0-40°C)
	EnoceanDPTBoolOnOffImpl datapoint_learnonoff;
	
	// constructor
	public EntityEEP_A52001Impl(ESP3Host esp3Host, EnoceanId id, String name, String displayName, String display, String manufacturer)
	{
		super(name, displayName, display, manufacturer);
		
		this.esp3Host = esp3Host;		
		this.id = id;	
		this.setWritable(true);
		this.setReadable(true);
		
		// Create and add new datapoint for the percentage value of the set point
		datapoint_percent = new EnoceanDPTIntPercImpl("BatteryPoweredActuatorSetPointPercentage", "Set Point Percantage", "0-100%", true, true);
		datapoint_percent.addTranslation("de-DE", TranslationAttribute.displayName, "Sollwert Prozent");
		this.addDatapoint(datapoint_percent);
		
		// Create and add new datapoint for the temperature set point
		datapoint_temp = new EnoceanDPTRealTempImpl("BatteryPoweredActuatorSetPointTemperature", "Set Point Temperature", "0-40°C", true, true);
		datapoint_temp.addTranslation("de-DE", TranslationAttribute.displayName, "Sollwert Temperatur");
		this.addDatapoint(datapoint_temp);
		
		// Create and add new datapoint for current temperature
		datapoint_currenttemp = new EnoceanDPTRealTempImpl("BatteryPoweredActuatorCurrentTemperature", "Current Temperature", "0-40°C", true, false);
		datapoint_currenttemp.addTranslation("de-DE", TranslationAttribute.displayName, "Istwert Temperatur");
		this.addDatapoint(datapoint_currenttemp);
		
		// Create and add new datapoint for set point mode
		datapoint_setpoint = new EnoceanDPTBoolOnOffImpl("BatteryPoweredActuatorSetPointMode", "Set Point Selection", "0=Percatage/1=Termperature", true, true);
		datapoint_setpoint.addTranslation("de-DE", TranslationAttribute.displayName, "Sollwert Modus");
		this.addDatapoint(datapoint_setpoint);
		
		// Create and add new datapoint for teach in mode
		datapoint_learnonoff = new EnoceanDPTBoolOnOffImpl("TeachIn", "TeachIn mode", "On/Off", true, true);
		datapoint_learnonoff.addTranslation("de-DE", TranslationAttribute.displayName, "Lernmodus");
		this.addDatapoint(datapoint_learnonoff);
		
		// Add a new watchdog for value changes
		esp3Host.addWatchDog(id, new EnoceanWatchdog() {
			
			@Override
			public void notifyWatchDog(BasicPacket packet) {
				// Check if the packet is of type 4BS like specified for this devices
				if (packet instanceof RadioPacket4BS) {					
		            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
		            // Get necessary information from the packet
		            Bool learnbit = new Bool(!Bits.isBitSet(radioPacket4BS.getDb0(), 3));
		            Int percentage = new Int(radioPacket4BS.getDb1());
		            Real temperature = temperatureByteToReal(radioPacket4BS.getDb3());
		          		            
		            log.info("EnOcean device with ID " +radioPacket4BS.getSenderId().toString() + ": TeachIn Mode " 
		            		+EncodingsImpl.getInstance().getEncoding(EncodingPressedReleased.HREF).getName(learnbit)); 
		            log.info("EnOcean device with ID " +radioPacket4BS.getSenderId().toString() + ": Set Point Percantage " 
		            		+percentage.toString());
		            log.info("EnOcean device with ID " +radioPacket4BS.getSenderId().toString() + ": Current Temperature " 
		            		+percentage.toString());
		            // write the received values to the datapoints
		            datapoint_learnonoff.writeObject(learnbit);
		            datapoint_percent.writeObject(percentage);
		            datapoint_currenttemp.writeObject(temperature);
		            EntityEEP_A52001Impl.this.notifyObservers();
		        }					
			}
		});
	}	
	
	// helper function to convert a temperature in °C to the byte format of the 4BS telegram
	private Real temperatureByteToReal(byte temperature){
		double real = ((double)temperature)*40/255;
		Real temp = new Real(real);
		return temp;		
	}
	
	// helper function to convert the byte format of the 4BS telegram to a temperature in °C
	private byte temperatureDoubleToByte(double temperature){
		int inttemp = (int)(temperature*255/40);
		byte temp = (byte)(inttemp&0xFF);
		return temp;		
	}
	
	@Override
	public void initialize(){
		super.initialize();		
	}

	@Override
	public void writeObject(Obj input){
		super.writeObject(input);			
		
		// check if the entity is writable and create a new 4BS telegram with the necessary information
		if (this.datapoint_percent.isWritable())
		{
			byte value=0x00;		
			
			if (input instanceof Real)
			{
				value = temperatureDoubleToByte(input.getReal());
			}
			else if (input instanceof Int)
			{
				value = (byte)(input.getInt()&0xFF);				
			}			
			
			ByteBitSet db0 = new ByteBitSet((byte)0x00);
			ByteBitSet db1 = new ByteBitSet((byte)0x00);
			byte db2 = temperatureDoubleToByte(datapoint_currenttemp.value().getReal());
			byte db3;
			
			db0.setBit(3, !datapoint_learnonoff.value().getBool());
			db1.setBit(2 ,datapoint_setpoint.value().getBool());
			
			if(datapoint_setpoint.value().getBool()){ // off = valve pos (0-100), on = temp (0-40°C)
				db3 = temperatureDoubleToByte(datapoint_temp.value().getReal());
			} else {
				db3 = (byte)(datapoint_percent.value().getInt()&0xFF);
			}		
			
			byte data[] = {(byte) db0.getByte(), (byte) db1.getByte(), db2, db3};
		    			
			// create the 4BS telegram
			RadioPacket4BS radio4BS = new RadioPacket4BS(data, this.esp3Host.getSenderId().toInt(), (byte) 0x00, (byte) 0x03, this.id.toInt(), (byte) 0xFF, (byte) 0x00);
		        
			// send the 4BS telegram
		    BasicPacket packet = radio4BS;
		    log.info("Send packet: " + packet.toString());
		    esp3Host.sendRadio(packet);  			
		}      
	}

	@Override
	public void refreshObject(){	
		// here we need to read from the bus, only if the read flag is set at the data point
		if(datapoint_percent.value().isReadable())	
		{
			// read the information of the entity
		}

		// run refresh from super class
		super.refreshObject();		
	}
	
}
