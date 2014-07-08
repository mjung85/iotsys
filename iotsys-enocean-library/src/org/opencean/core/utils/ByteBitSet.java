package org.opencean.core.utils;

public class ByteBitSet {

    private byte data = 0;

    public ByteBitSet() {
    }

    public ByteBitSet(byte data) {
        this.data = data;
    }

    public void setBit(int pos, boolean bit) {
        data = Bits.setBit(data, pos, bit);
    }

    public byte getByte() {
        return data;
    }
}
