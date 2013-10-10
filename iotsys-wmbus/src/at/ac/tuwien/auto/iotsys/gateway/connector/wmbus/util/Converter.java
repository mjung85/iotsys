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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.util;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class Converter {

	public static String convertByteArrayToString(byte[] byteArr) { 
		 StringBuffer hexString = new StringBuffer();
         for (int i = 0; i < byteArr.length; i++) {
             String hex = Integer.toHexString(0xFF & byteArr[i]);
             if (hex.length() == 1) {
                 // could use a for loop, but we're only dealing with a single byte
                 hexString.append('0');
             }
             hexString.append(hex + " ");
         }
         return hexString.toString();
	}
	
	public static byte[] convertStringArrToByteArray(String[] inputStr) {
		return Converter.convertStringListToByteArray(Arrays.asList(inputStr));
	}
	
	public static byte[] convertStringListToByteArray(List<String> inputStr) {
		byte[] fByteArr = new byte[inputStr.size()];
		for(int i = 0; i < inputStr.size(); i++) {
			// this transformation also deals with negative values
			// so not all 128 Bits are used
			// FF --> 00 FF
			// -FF --> FF 01
			// thus we have to check which part of the result we have to use
			byte[] byteArrTemp = new BigInteger(inputStr.get(i), 16).toByteArray();
			fByteArr[i] = byteArrTemp[0];
			if(byteArrTemp.length > 1) {
				fByteArr[i] = byteArrTemp[1];
			}
		}
		return fByteArr;
	}
	
	public static String convertListToString(List<String> inputStr) {
		String retString = "";
		for(int i = 0; i < inputStr.size(); i++) {
			retString = retString + inputStr.get(i);
		}
		return retString;
	}
	
	public static int hexToInt(String s) {
		return Integer.parseInt(s, 16);
	}
}
