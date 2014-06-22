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

import obix.Obj;

import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.datapoint.impl.EnoceanDPTBoolOnOffImpl;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.EntityEEP_F60201;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.enocean.entity.EntityImpl;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanConnector;

public class EntityEEP_F60201Impl extends EntityImpl implements EntityEEP_F60201
{
	private EnoceanConnector connector;	// TODO change to the new API
	private String hexAddress;			// TODO change to the new API
	EnoceanDPTBoolOnOffImpl datapoint_lightonoff;

	public EntityEEP_F60201Impl(EnoceanConnector connector, String hexAddress, String name, String displayName, String display, String manufacturer)
	{
		super(name, displayName, display, manufacturer);
		
		this.connector = connector;		// TODO change to the new API
		this.hexAddress = hexAddress;	// TODO change to the new API	
		
		datapoint_lightonoff = new EnoceanDPTBoolOnOffImpl("WallTransmitterChA", "Switch, Channel A", "On / Off", true, false);
		datapoint_lightonoff.addTranslation("de-DE", TranslationAttribute.displayName, "Schalten, Kanal A");
		this.addDatapoint(datapoint_lightonoff);		
		
//		EnoceanDPTBoolOnOffImpl datapoint_learnonoff = new EnoceanDPTBoolOnOffImpl("WallTransmitterTeachIn", "TeachIn mode", "On / Off", true, false);
//		datapoint_lightonoff.addTranslation("de-DE", TranslationAttribute.displayName, "Lernmodus");
//		this.addDatapoint(datapoint_learnonoff);		
	}	
	
	@Override
	public void initialize(){
		super.initialize();
		// But stuff here that should be executed after object creation
	}

	@Override
	public void writeObject(Obj input){
		super.writeObject(input);
		// TODO insert Enocean send method
	}

	@Override
	public void refreshObject(){	
		// here we need to read from the bus, only if the read flag is set at the data point
		if(datapoint_lightonoff.value().isReadable())	
		{
//			boolean value = connector.readBool(groupAddress); // TODO read value
//			
//			this.value().set(value);
//			this.value().setNull(false);
//			this.encoding().setNull(false);
		}

		// run refresh from super class
		super.refreshObject();		
	}
	
}
