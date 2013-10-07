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

package at.ac.tuwien.auto.iotsys.gateway.util;

import org.json.JSONException;
import org.json.JSONML;
import org.json.JSONObject;

public class JsonUtil {
	public static void main(String[] args){
//		String xml = "<str val=\"DB08::2\"/>";
//		String xml = "<obj href=\"/humidity\" is=\"iot:HumiditySensor\"><real name=\"value\" href=\"value\" val=\"50.0\" unit=\"obix:units/percent\"/></obj>";
		String xml = "<obj href=\"/lightIntensity1\" is=\"iot:LightIntensitySensor\"><real name=\"value\" href=\"value\" val=\"1000.0\" unit=\"obix:units/lumen\"/></obj>";

		
//		String xml = "<obj is=\"obix:WatchIn\"><list name=\"hrefs\"><uri val=\"/VirtualDevices/virtualLight/value\" /></list></obj>";
		//String xml = "<obj is=\"obix:WatchOut\"><list><obj href=\"/testDevices/switch3\" is=\"iot:LightSwitchActuator\"><bool name=\"value\" href=\"/testDevices/switch3/value\" val=\"false\" writable=\"true\"/></obj></list></obj>";
		String json = "{\"is\":\"obix:WatchIn\",\"nodes\":[{\"nodes\":[{\"val\":\"/testDevices/switch3/value\",\"tag\":\"uri\"}],\"tag\":\"list\",\"name\":\"hrefs\"}],\"tag\":\"obj\"}";
		try {
			System.out.println(fromXMLtoJSON(xml));			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println(fromJSONtoXML(json));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String fromXMLtoJSON(String xml) throws JSONException{
		JSONObject json = JSONML.toJSONObject(xml);
		return json.toString();
	}
	
	public static String fromJSONtoXML(String jsonData ) throws JSONException{
		String data = "";
		if(jsonData != null){				
				JSONObject jsonObject = new JSONObject(jsonData);	
				data = JSONML.toString(jsonObject);				
		}
		return data;
	}

}
