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
import org.opencean.core.packets.RadioPacket1BS;
import org.opencean.core.utils.Bits;

import obix.Bool;
import obix.Obj;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTBoolOnOffImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTBoolOpenClosedImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.EntityEEP_D50001;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.impl.EnoceanEntityImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.EncodingOnOff;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.EncodingOpenClosed;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.general.encoding.impl.EncodingsImpl;

public class EntityEEP_D50001Impl extends EnoceanEntityImpl implements EntityEEP_D50001
{	
	private static Logger log = Logger.getLogger(EntityEEP_D50001Impl.class.getName());
		
	protected final ESP3Host esp3Host;	
	protected final EnoceanId id;			
	EnoceanDPTBoolOpenClosedImpl datapoint_openclosed;
	EnoceanDPTBoolOnOffImpl datapoint_learnonoff;

	// constructor
	public EntityEEP_D50001Impl(ESP3Host esp3Host, EnoceanId id, String name, String displayName, String display, String manufacturer)
	{
		super(name, displayName, display, manufacturer);
		
		this.esp3Host = esp3Host;		
		this.id = id;	
		this.setWritable(true);
		this.setReadable(true);
		
		// Create and add new datapoint for the single input contact
		datapoint_openclosed = new EnoceanDPTBoolOpenClosedImpl("SingleInputContact", "Single Input Contact", "Open/Closed", this, false, false);
		datapoint_openclosed.addTranslation("de-DE", TranslationAttribute.displayName, "Kontaktsensor");
		this.addDatapoint(datapoint_openclosed);		
		
		// Create and add new datapoint for the teach in mode
		datapoint_learnonoff = new EnoceanDPTBoolOnOffImpl("TeachIn", "TeachIn mode", "On/Off", this, false, false);
		datapoint_learnonoff.addTranslation("de-DE", TranslationAttribute.displayName, "Lernmodus");
		this.addDatapoint(datapoint_learnonoff);		
		
		// Add a new watchdog for value changes
		esp3Host.addWatchDog(id, new EnoceanWatchdog() {
			
			@Override
			public void notifyWatchDog(BasicPacket packet) {
				if (packet instanceof RadioPacket1BS) {
					RadioPacket1BS radioPacket1BS = (RadioPacket1BS) packet;
		            Bool contactbit = new Bool(Bits.isBitSet(radioPacket1BS.getDataByte(), 0));
		            Bool learnbit = new Bool(!Bits.isBitSet(radioPacket1BS.getDataByte(), 3)); 
		            		            
		            log.info("EnOcean device with ID " +radioPacket1BS.getSenderId().toString() + ": Contact " 
		            		+EncodingsImpl.getInstance().getEncoding(EncodingOpenClosed.HREF).getName(contactbit));
		            datapoint_openclosed.setValue(contactbit); 
		            
		            log.info("EnOcean device with ID " +radioPacket1BS.getSenderId().toString() + ": TeachIn Mode " 
		            		+EncodingsImpl.getInstance().getEncoding(EncodingOnOff.HREF).getName(learnbit)); 
		            datapoint_learnonoff.setValue(learnbit); 
		            EntityEEP_D50001Impl.this.notifyObservers();		            
		        }					
			}
		});
	}	
	
	@Override
	public void initialize(){
		super.initialize();
		// Put stuff here that should be executed after object creation
	}

	@Override
	public void writeObject(Obj input){
		super.writeObject(input);			
		
		if (this.isWritable())
		{
//			no data can be written to the single input contact
		}
      
	}

	@Override
	public void refreshObject(){	
		// here we need to read from the bus, only if the read flag is set at the data point
		if(this.isReadable())	
		{
			//	value can not be read from the single input contact
		}

		// run refresh from super class
		super.refreshObject();		
	}
	
}
