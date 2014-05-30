package org.opencean.core;

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
}
