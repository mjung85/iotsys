package org.opencean.core.packets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.opencean.core.EnoceanBufferedDummieConnector;
import org.opencean.core.packets.Header;
import org.opencean.core.packets.RadioPacket;

public class HeaderTest {

    public static final byte[] testHeader = new byte[] { 0x00, 0x0F, 0x07, 0x01, 0x2B };
    private Header header;

    @Before
    public void setupTestHeader() {
        EnoceanBufferedDummieConnector buffer = new EnoceanBufferedDummieConnector(2048);
        buffer.write(testHeader);
        header = Header.from(buffer);
    }

    @Test
    public void testToBytes() {
        assertEquals("Lenth", testHeader.length, header.toBytes().length);
    }

    @Test
    public void testIsValid() {
        assertTrue("isValid", header.isValid());
    }

    @Test
    public void testCheckCrc8() {
        header.checkCrc8();
    }

    @Test
    public void testGetPacketType() {
        assertEquals(RadioPacket.PACKET_TYPE, header.getPacketType());
    }

    @Test
    public void testGetDataLength() {
        assertEquals(15, header.getDataLength());
    }

    @Test
    public void testGetOptionalDataLength() {
        assertEquals(7, header.getOptionalDataLength());
    }

}
