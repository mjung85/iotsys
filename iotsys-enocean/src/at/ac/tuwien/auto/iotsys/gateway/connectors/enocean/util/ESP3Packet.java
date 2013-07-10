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

public class ESP3Packet
{
	private ESP3PacketHeader header;
	private byte[] data;
	private byte[] optData;
	private byte[] allData;
	public ESP3Telegram telegram;
	
	public ESP3Packet (ESP3PacketHeader header, byte[] allData)
	{
		this.allData = allData;
		this.header = header;
		this.data = new byte[header.getDataLength()];
		this.optData = new byte[header.getOptionalDataLength()];
		this.telegram = new ESP3Telegram(allData, header.getDataLength(), header.getOptionalDataLength());
		System.arraycopy (allData, 0, data, 0, header.getDataLength());
		System.arraycopy (allData, header.getDataLength(), optData, 0, header.getOptionalDataLength());
	}
	
    public ESP3PacketHeader.PacketType getPacketType ()
    {
        return header.getPacketType ();
    }

    public byte[] getData()
    {
        if (data == null)
            return new byte[] {};

        return data.clone();
    }

    public byte[] getOptionalData()
    {
        if (optData == null)
            return new byte[] {};

        return optData.clone();
    }
    
    public ESP3PacketHeader getHeader()
    {
    	return header;
    }
}
