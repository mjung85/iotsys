package org.opencean.core.packets;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.QueryIdCommand;
import org.opencean.core.utils.ByteArray;

public class QueryIdCommandTest {

    // Strangely the CRC8h is EF in the ESP3 spec. My algorithm says it should
    // be -6 decimal (whatever that is in hex)
    private static final byte[] testHeader = new byte[] { 0x00, 0x0C, 0x00, 0x07, (byte) -6 };
    private static final byte[] testPayload = new byte[] { (byte) 0x00, (byte) 0x04, (byte) 0x07, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x00, 0x00, (byte) 0x00, 0x65 };

    @Test
    public void testToBytes() {
        byte[] expected = new ByteArray().addByte(BasicPacket.SYNC_BYTE).addBytes(testHeader).addBytes(testPayload).getArray();
        QueryIdCommand command = new QueryIdCommand();
        assertArrayEquals(expected, command.toBytes());
    }

}
