package org.opencean.core.packets;

public enum LearnButtonState {
    PRESSED(0),
    NOTPRESSED(1);

    private final int enumvalue;

    LearnButtonState(int value) {
        this.enumvalue = value;
    }

    LearnButtonState(byte value) {
        this.enumvalue = value;
    }

    public byte toByte() {
        return (byte) enumvalue;
    }

    @Override
    public String toString() {
        return ( enumvalue == 0 ) ? "Pressed" : "Not Pressed";
    }
}