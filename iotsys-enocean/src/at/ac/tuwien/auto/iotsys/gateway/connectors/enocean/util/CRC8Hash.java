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


public final class CRC8Hash
{
	private static final byte[] CRC8_TABLE = calculateCRC8Table();
	
	private CRC8Hash () {}
	
	private static byte[] calculateCRC8Table()
	{
		byte[] table = new byte[256];
		int generatorPoly = 0x07;
		int result;
		
		for (int i = 1; i < table.length; i++)
		{
			result = i;
			
			for (int j = 0; j < 8; j++)
			{
				if ((result & 0x80) != 0)
				{
					result = (result << 1) ^ generatorPoly;
				}
				else
				{
					result = result << 1;
				}
			}
			table[i] = (byte)result; 
		}
		return table;
	}
	
	public static byte calculate(byte[] data)
	{
		return calculate(data, 0, data.length);
	}
	
	public static byte calculate(byte[] data, int offset, int len)
	{
		byte crc = 0;
		
		for (int i = offset; i < (offset + len); i++)
		{
			crc = CRC8_TABLE[(crc ^ data[i]) & 0xFF];
		}
		return crc;
	}
}
