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

package org.opencean.core;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.ParameterAddress;
import org.opencean.core.common.values.ByteStateAndStatus;
import org.opencean.core.packets.DataGenerator;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RadioPacketRPS;

public class StateChanger {

    public StateChanger() {

    }

    public byte[] changeState(String newState, ParameterAddress parameterAddress, String eep) {

        if (eep.equals(EEPId.EEP_F6_02_02.toString()) || eep.equals(EEPId.EEP_F6_02_01.toString())) {
            RadioPacket packet = new RadioPacket();
            DataGenerator dataGen;
            byte[] data;

            if (ByteStateAndStatus.getByteFor(newState) != ByteStateAndStatus.ERROR) {
                dataGen = new DataGenerator(RadioPacketRPS.RADIO_TYPE, ByteStateAndStatus.getByteFor(newState),
                        (EnoceanParameterAddress) parameterAddress, ByteStateAndStatus.PRESSED);
                data = dataGen.getData();
                packet = new RadioPacket(data, (byte) 0x03, 0xFFFFFFFF, (byte) 0xFF, (byte) 0x00);
            }
            return packet.toBytes();
        }
        return null;
    }
    
    public RadioPacket changeState(String newState, EnoceanId parameterAddress, String eep) {

        if (eep.equals(EEPId.EEP_F6_02_02.toString()) || eep.equals(EEPId.EEP_F6_02_01.toString())) {
            RadioPacket packet = new RadioPacket();
            DataGenerator dataGen;
            byte[] data;

            if (ByteStateAndStatus.getByteFor(newState) != ByteStateAndStatus.ERROR) {
                dataGen = new DataGenerator(RadioPacketRPS.RADIO_TYPE, ByteStateAndStatus.getByteFor(newState),
                        parameterAddress, ByteStateAndStatus.PRESSED);
                data = dataGen.getData();
                packet = new RadioPacket(data, (byte) 0x03, 0xFFFFFFFF, (byte) 0xFF, (byte) 0x00);
            }
            return packet;
        }
        return null;
    }      
}
