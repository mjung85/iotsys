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

package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.test;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.Telegram;
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;

public class ParseHeaderTest {

	public static void main(String[] args) throws Exception {
		String telegram = "68 6A 6A 68 08 01 72 43 53 93 07 65 32 10 04 CA 00 00 00 0C 05 14 00 00 00 0C 13 13 20 00 00 0B 22 01 24 03 04 6D 12 0B D3 12 32 6C 00 00 0C 78 43 53 93 07 06 FD 0C F2 03 01 00 F6 01 0D FD 0B 05 31 32 4D 46 57 01 FD 0E 00 4C 05 14 00 00 00 4C 13 13 20 00 00 42 6C BF 1C 0F 37 FD 17 00 00 00 00 00 00 00 00 02 7A 25 00 02 78 25 00 3A 16";
		
		Telegram tel = new Telegram();
		tel.createTelegram(telegram);
		tel.debugOutput();
		
		byte[] result = Converter.convertStringListToByteArray(tel.getBody().getBodyPayload().getPayloadAsList());
		
		System.out.println("Payload: " + Converter.convertByteArrayToString(result));				
	
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("-----------------------------------------------------------------------");		
	}
}
