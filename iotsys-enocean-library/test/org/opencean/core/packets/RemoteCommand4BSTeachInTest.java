package org.opencean.core.packets;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.packets.RemoteCommand4BSTeachIn;

public class RemoteCommand4BSTeachInTest {

    @Test
    public void toBytes() {
        RemoteCommand4BSTeachIn teachIn = new RemoteCommand4BSTeachIn();
        byte[] bytes = teachIn.toBytes();
        assertEquals("Length", 1 + 5 + 6, bytes.length);
    }

}
