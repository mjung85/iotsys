package org.opencean.core.packets;

import org.opencean.core.utils.Bits;

public class RadioPacket4BS extends RadioPacket {

    public static final byte RADIO_TYPE = (byte) 0xA5;

    public static final int DATA_LENGTH = 10;

    private byte db0;
    private byte db1;
    private byte db2;
    private byte db3;

    public RadioPacket4BS() {
    }

    public RadioPacket4BS(RawPacket rawPacket) {
        super(rawPacket);
    }

    @Override
    protected void parseData() {
        super.parseData();
        byte[] data = payload.getData();
        db3 = data[1];
        db2 = data[2];
        db1 = data[3];
        db0 = data[4];
    }

    public byte getDb0() {
        return db0;
    }

    public byte getDb1() {
        return db1;
    }

    public byte getDb2() {
        return db2;
    }

    public byte getDb3() {
        return db3;
    }

    public void setDb1(byte db1) {
        this.db1 = db1;
    }

    public boolean isTeachInMode() {
        return Bits.isBitSet(db0, 4);
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(", [db0=%02X, db1=%02X, db2=%02X, db3=%02X, teachIn=%s]", db0, db1, db2, db3, isTeachInMode());
    }
}
