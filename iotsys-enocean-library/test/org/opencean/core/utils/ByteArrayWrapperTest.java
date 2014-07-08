package org.opencean.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.utils.ByteArray;

public class ByteArrayWrapperTest {

    @Test
    public void testSetInt() {
        ByteArray wrapper = new ByteArray(new byte[4]);
        wrapper.setInt(10, 0);
        byte[] array = wrapper.getArray();
        assertEquals("length", 4, array.length);
        assertEquals(0, array[0]);
        assertEquals(0, array[1]);
        assertEquals(0, array[2]);
        assertEquals(10, array[3]);
    }

    @Test
    public void testAddInt() {
        ByteArray wrapper = new ByteArray();
        wrapper.addInt(10);
        byte[] array = wrapper.getArray();
        assertEquals("length", 4, array.length);
        assertEquals(0, array[0]);
        assertEquals(0, array[1]);
        assertEquals(0, array[2]);
        assertEquals(10, array[3]);
    }

    @Test
    public void testAddShort() {
        ByteArray wrapper = new ByteArray();
        wrapper.addShort((short) 10);
        byte[] array = wrapper.getArray();
        assertEquals("length", 2, array.length);
        assertEquals(0, array[0]);
        assertEquals(10, array[1]);
    }

    @Test
    public void testSetByte() {
        ByteArray wrapper = new ByteArray(new byte[1]);
        wrapper.setByte((byte) 10, 0);
        byte[] array = wrapper.getArray();
        assertEquals("length", 1, array.length);
        assertEquals(10, array[0]);
    }

    @Test
    public void testAddBytes() {
        ByteArray wrapper = new ByteArray();
        wrapper.addBytes(new byte[] { 1, 2, 3 });
        byte[] array = wrapper.getArray();
        assertEquals("length", 3, array.length);
        assertEquals(1, array[0]);
        assertEquals(2, array[1]);
        assertEquals(3, array[2]);
    }

    @Test
    public void testAddByte() {
        ByteArray wrapper = new ByteArray();
        wrapper.addByte((byte) 10);
        byte[] array = wrapper.getArray();
        assertEquals("length", 1, array.length);
        assertEquals(10, array[0]);
    }

}
