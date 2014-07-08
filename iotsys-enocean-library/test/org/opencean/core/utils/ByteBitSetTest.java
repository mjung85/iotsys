package org.opencean.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.utils.ByteBitSet;

public class ByteBitSetTest {

    @Test
    public void testSetBit() {
        ByteBitSet bitSet = new ByteBitSet();
        bitSet.setBit(0, true);
        assertEquals(1, bitSet.getByte());
    }

}
