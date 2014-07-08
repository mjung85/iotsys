package org.opencean.core.eep;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.opencean.core.PacketFactory;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.ButtonState;
import org.opencean.core.common.values.Value;
import org.opencean.core.eep.RockerSwitch;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.Header;
import org.opencean.core.packets.Payload;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RadioPacketRPS;
import org.opencean.core.packets.RawPacket;
import org.opencean.core.utils.Bits;

public class RockerSwitchTest {

    private RockerSwitch rockerSwitch;

    @Before
    public void createRockerSwitch() {
        rockerSwitch = new RockerSwitch();
    }

    @Test
    public void testAIPressed() {
        Map<EnoceanParameterAddress, Value> values = pressAI();
        assertEquals("size", 1, values.size());
        Entry<EnoceanParameterAddress, Value> value = values.entrySet().iterator().next();
        assertEquals(ButtonState.PRESSED, value.getValue());
        assertEquals(new EnoceanParameterAddress(EnoceanId.fromString("00:00:00:00"), RockerSwitch.CHANNEL_A, "I"), value.getKey());
    }

    @Test
    public void testAIReleased() {
        pressAI();
        byte dataByte = 0;
        byte statusByte = 0;
        statusByte = Bits.setBit(statusByte, 5, false); // T21
        statusByte = Bits.setBit(statusByte, 4, true); // NU
        RawPacket rawPacket = createRawPacket(dataByte, statusByte);
        BasicPacket basicPacket = PacketFactory.createFrom(rawPacket);
        Map<EnoceanParameterAddress, Value> values = rockerSwitch.parsePacket(basicPacket);
        assertEquals("size", 1, values.size());
        Entry<EnoceanParameterAddress, Value> value = values.entrySet().iterator().next();
        assertEquals(ButtonState.RELEASED, value.getValue());
        assertEquals(new EnoceanParameterAddress(EnoceanId.fromString("00:00:00:00"), RockerSwitch.CHANNEL_A, "I"), value.getKey());
    }

    private Map<EnoceanParameterAddress, Value> pressAI() {
        byte dataByte = 0;
        dataByte = Bits.setBit(dataByte, 4, true); // PRESSED
        byte statusByte = 0;
        statusByte = Bits.setBit(statusByte, 5, false); // T21
        statusByte = Bits.setBit(statusByte, 4, true); // NU
        RawPacket rawPacket = createRawPacket(dataByte, statusByte);
        BasicPacket basicPacket = PacketFactory.createFrom(rawPacket);
        Map<EnoceanParameterAddress, Value> values = rockerSwitch.parsePacket(basicPacket);
        return values;
    }

    private RawPacket createRawPacket(byte dataByte, byte statusByte) {
        Header header = new Header(RadioPacket.PACKET_TYPE, (short) 7, (byte) 0);
        Payload payload = new Payload();
        payload.setData(new byte[] { RadioPacketRPS.RADIO_TYPE, dataByte, 0, 0, 0, 0, statusByte });
        RawPacket rawPacket = new RawPacket(header, payload);
        return rawPacket;
    }

}
