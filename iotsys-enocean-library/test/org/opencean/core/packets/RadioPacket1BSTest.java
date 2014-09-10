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

package org.opencean.core.packets;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.Header;
import org.opencean.core.packets.Payload;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RawPacket;
import org.opencean.core.utils.Bits;

public class RadioPacket1BSTest {

    @Test
    public void testRadioPacket1BSparse() {
    	BasicPacket packet = createRawPacket((byte)0x01);
    	if (packet instanceof RadioPacket1BS) {
			RadioPacket1BS radioPacket1BS = (RadioPacket1BS) packet;
            boolean contactbit = Bits.isBitSet(radioPacket1BS.getDataByte(), 0);
            boolean learnbit = !Bits.isBitSet(radioPacket1BS.getDataByte(), 3); 
            		            
            assertEquals(true, contactbit);
            assertEquals(true, learnbit);		            
        } else{
        	Assert.fail();
        }    	
    }
    
    private BasicPacket createRawPacket(byte dataByte) {
        Header header = new Header(RadioPacket.PACKET_TYPE, (short) 7, (byte) 0);
        Payload payload = new Payload();
        payload.setData(new byte[] { RadioPacket1BS.RADIO_TYPE, dataByte, 0, 0, 0, 0, (byte) 0x00 });
        RawPacket rawPacket = new RawPacket(header, payload);
        BasicPacket basicPacket = new RadioPacket1BS(rawPacket);
        return basicPacket;
    }

}
