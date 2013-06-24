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

package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

/*
 * Example Packets:
 * Switch On (Press):   55 00 07 07 01 7a f6 50 00 10 20 2b 30 01 ff ff ff ff 36 00 92 
 * Switch On (Release): 55 00 07 07 01 7a f6 00 00 10 20 2b 20 01 ff ff ff ff 36 00 21
 * */

public final class CRC8Test {

    public static byte[] hexStringToByteArray(final String s)
    {
    	final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
        	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
	public static void main(final String[] args)
	{
		byte crc8;
		
		// test over header
		final String headerString = "00070701"; // crc value should be 0x7a
		crc8 = CRC8Hash.calculate(hexStringToByteArray(headerString));
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		
		// test over data + opt data
		final String dataString = "f6500010202b3001ffffffff3600"; // crc value should be 0x92
		crc8 = CRC8Hash.calculate(hexStringToByteArray(dataString));
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		
		// test over range in whole frame
		final String wholeFrameString = "55000707017af6000010202b2001ffffffff360021";
		crc8 = CRC8Hash.calculate(hexStringToByteArray(wholeFrameString), 1, 4); // for header
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		crc8 = CRC8Hash.calculate(hexStringToByteArray(wholeFrameString), 6, 14); // for data + opt data
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
	}
}
