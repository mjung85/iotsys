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

package at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.test;

import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.AESEncrypt;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.telegrams.Telegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.wmbus.util.Converter;

public class ParseHeaderTest {

	public static void main(String[] args) throws Exception {
		String telegram = "3E 44 2D 4C 74 44 00 15 1E 02 7A 8A 00 30 85 47 4E AB 1A ED 7B 9A C4 82 B2 FE 31 45 03 1A B3 41 5A 69 F2 75 B3 9B D6 04 11 53 EB BE 67 76 CA 08 4F 46 04 18 2B AD 13 21 FA FB 9B 7E 12 02 30";
		
		Telegram tel = new Telegram();
		tel.createTelegram(telegram, false);
		tel.debugOutput();
		
		String key = "66 77 66 77 66 77 66 77 66 77 66 77 66 77 66 77";
		
		byte[] keyArr = Converter.convertStringArrToByteArray(key.split(" "));
		byte[] initCTRVArr = tel.getAESCBCInitVector();
		byte[] payloadArr = Converter.convertStringListToByteArray(tel.getBody().getBodyPayload().getPayloadAsList());
		
		System.out.println("Key: " + Converter.convertByteArrayToString(keyArr));
		System.out.println("CTR-Init-Vector: " + Converter.convertByteArrayToString(initCTRVArr));
		System.out.println("Payload: " + Converter.convertByteArrayToString(payloadArr));
		
		byte[] result = AESEncrypt.decrypt(payloadArr, keyArr, initCTRVArr);
		
		System.out.println("Result: " + Converter.convertByteArrayToString(result));
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("-----------------------------------------------------------------------");
		
		// complete
		//telegram = "2E 44 93 15 78 56 34 12 33 03 33 63 7A 2A 00 20 05 59 23 C9 5A AA 26 D1 B2 E7 49 3B 2A 8B 01 3E C4 A6 F6 D3 52 9B 52 EA 6D EF C9 55 B2 9D 6D 69 EB F3 EC 8A";
		
		// only AES PART (with sig, with crc)
		//telegram = "20 05 59 23 C9 5A AA 26 D1 B2 E7 49 3B 2A 8B 01 3E C4 A6 F6 D3 52 9B 52 EA 6D EF C9 55 B2 9D 6D 69 EB F3 EC 8A";
		
		// only AES PART (without sig, with crc)
		//telegram = "59 23 C9 5A AA 26 D1 B2 E7 49 3B 2A 8B 01 3E C4 A6 F6 D3 52 9B 52 EA 6D EF C9 55 B2 9D 6D 69 EB F3 EC 8A";
			
		// only AES PART (with sig, without crc)
		//telegram = "20 05 59 23 C9 5A AA 26 D1 B2 E7 49 3B 01 3E C4 A6 F6 D3 52 9B 52 EA 6D EF C9 9D 6D 69 EB F3";
		
		// only AES PART (without sig, without crc)
		telegram = "59 23 C9 5A AA 26 D1 B2 E7 49 3B 01 3E C4 A6 F6 D3 52 9B 52 EA 6D EF C9 9D 6D 69 EB F3 00 00 00";
		
		// only AES PART (without sig, without crc)
		//telegram = "20 05 59 23 C9 5A AA 26 D1 B2 E7 49 3B";	
		
		key = "01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F 11";
		
		String initCTRVStr = "93 15 78 56 34 12 33 03 2A 2A 2A 2A 2A 2A 2A 2A";
		
		keyArr = Converter.convertStringArrToByteArray(key.split(" "));
		initCTRVArr = Converter.convertStringArrToByteArray(initCTRVStr.split(" "));
		payloadArr = Converter.convertStringArrToByteArray(telegram.split(" "));
		
		System.out.println("Key: " + Converter.convertByteArrayToString(keyArr));
		System.out.println("CTR-Init-Vector: " + Converter.convertByteArrayToString(initCTRVArr));
		System.out.println("Payload: " + Converter.convertByteArrayToString(payloadArr));
		
		result = AESEncrypt.decrypt(payloadArr, keyArr, initCTRVArr);
		
		System.out.println("Result: " + Converter.convertByteArrayToString(result));
	}
}
