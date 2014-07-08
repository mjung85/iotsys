package org.opencean.core.utils;

public class Bits {

    public static boolean isBitSet(byte b, int pos) {
        return ((b >> pos) & 1) == 1;
    }

    public static int getBit(byte b, int pos) {
        return ((b >> pos) & 1);
    }

    public static boolean getBool(byte b, int pos) {
        return ((b >> pos) & 1) == 1;
    }

    public static boolean isBitSet(short s, int pos) {
        return ((s >> pos) & 1) == 1;
    }

    public static byte setBit(byte b, int pos, boolean bit) {
        if (bit) {
            b = (byte) (b | (1 << pos));
        } else {
            b = (byte) (b & ~(1 << pos));
        }
        return b;
    }

}
