package org.opencean.core.packets;

public class RadioPacketRPS extends RadioPacket {

    public static final byte RADIO_TYPE = (byte) 0xF6;
    private byte dataByte;

    public RadioPacketRPS(RawPacket rawPacket) {
        super(rawPacket);
    }

    @Override
    public void parseData() {
        super.parseData();
        dataByte = payload.getData()[1];
    }

    public byte getDataByte() {
        return dataByte;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
