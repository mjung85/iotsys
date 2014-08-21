package org.opencean.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.opencean.core.utils.CircularByteBuffer;

public class CircularByteBufferTest {

    private static final int SIZE = 10;
    private CircularByteBuffer buffer;

    @Before
    public void createBuffer() {
        buffer = new CircularByteBuffer(SIZE);
    }

    @Test
    public void testReadAndWriteParallel() {
        WriteThreeBytesThread writeThread = new WriteThreeBytesThread(buffer);
        writeThread.start();
        assertEquals(10, buffer.get());
        assertEquals(20, buffer.get());
        assertEquals(30, buffer.get());
    }

    @Test
    public void testReadWriteAndResetParallel() {
        buffer.mark();
        WriteThreeBytesThread writeThread = new WriteThreeBytesThread(buffer);
        writeThread.start();
        assertEquals(10, buffer.get());
        buffer.reset();
        assertEquals(10, buffer.get());
        assertEquals(20, buffer.get());
        assertEquals(30, buffer.get());
    }

    private static class WriteThreeBytesThread extends Thread {

        private CircularByteBuffer buffer;

        public WriteThreeBytesThread(CircularByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void run() {
            buffer.put((byte) 10);
            sleep();
            buffer.put((byte) 20);
            sleep();
            buffer.put((byte) 30);
        }

        private void sleep() {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

    }

    @Test
    public void testGet() {
        byte i = 5;
        buffer.put(i);
        byte result = buffer.get();
        assertEquals(i, result);
    }

    @Test
    public void testGetAndPutUpToSize() {
        for (byte i = 0; i < SIZE; i++) {
            buffer.put(i);
            byte result = buffer.get();
            assertEquals(i, result);
        }
    }

    @Test
    public void testGetAndPutOverSize() {
        for (byte i = 0; i < (SIZE * 2); i++) {
            buffer.put(i);
            byte result = buffer.get();
            assertEquals(i, result);
        }
    }

    @Test
    public void testGetShort() {
        short i = 5;
        buffer.put((byte) i);
        byte result = buffer.get();
        assertEquals(i, result);
    }

    @Test
    public void testGetByteArray() {
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        byte[] data = new byte[2];
        buffer.get(data);
        assertEquals(1, data[0]);
        assertEquals(2, data[1]);
    }

    @Test
    public void testMark() {
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        assertEquals(1, buffer.get());
        buffer.mark();
        assertEquals(2, buffer.get());
        buffer.reset();
        assertEquals(2, buffer.get());
        assertEquals(3, buffer.get());
    }

}
