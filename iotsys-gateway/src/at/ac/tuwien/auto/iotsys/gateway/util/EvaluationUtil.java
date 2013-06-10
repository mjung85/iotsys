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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import at.ac.tuwien.auto.iotsys.gateway.obix.objectbroker.ObjectBrokerImpl;

import com.csvreader.CsvWriter;

public class EvaluationUtil {


	public static void evaluation(){
			System.out.println("######### BEGIN EVALUATION ###########");
			
			new EvaluationGenerateContracts(ObjectBrokerImpl.getInstance(), false, true, false, false, false);
			
			
			HashMap<String, String> ipv6ContractList = new HashMap<String, String>();
			ipv6ContractList = ObjectBrokerImpl.getInstance().get_ipv6MappingTable();
			//Collections.sort(ipv6ContractList);
			List<String> sortedList = new ArrayList<String>();
			sortedList.addAll(ipv6ContractList.keySet());
			Collections.sort(sortedList);
			
			Iterator<String> iter = sortedList.iterator();
			
			try {
				
				CsvWriter csvOutput = new CsvWriter(new FileWriter("./csv/AvaliabelIpv6AddressRessources.csv", true), ';');	
				while (iter.hasNext()) {
					String key = iter.next();
					csvOutput.writeRecord(new String[] { key, ipv6ContractList.get(key)});
				}
				csvOutput.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (Entry<String, String> entry : ipv6ContractList.entrySet()) {

	        }		
	}
}
