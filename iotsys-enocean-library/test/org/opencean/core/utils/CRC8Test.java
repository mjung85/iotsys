package org.opencean.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.utils.CRC8;

public class CRC8Test {

    @Test
    public void testGetValue() {
        byte[] data = new byte[] { 1, 2, 3 };
        CRC8 crc8 = new CRC8();
        crc8.update(data, 0, 3);
        assertEquals(0x48, crc8.getValue());
    }

}
