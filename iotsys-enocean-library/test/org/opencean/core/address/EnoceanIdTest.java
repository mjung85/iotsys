package org.opencean.core.address;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.address.EnoceanId;

public class EnoceanIdTest {

    @Test
    public void fromString() {
        assertEquals(new EnoceanId(new byte[] { (byte) 0xAA, 0x01, (byte) 0xFF, 0x00 }), EnoceanId.fromString("AA:01:FF:00"));
    }

    @Test
    public void testToString() {
        byte[] id = new byte[] { 0x00, (byte) 0xA1, (byte) 0xb2, (byte) 0xc3 };
        assertEquals("00:A1:B2:C3", new EnoceanId(id).toString());
    }

}
