package org.opencean.core.packets;

import org.opencean.core.utils.ByteArray;

public class RadioPacket1BS extends RadioPacket {

    public static final byte RADIO_TYPE = (byte) 0xD5;

    private byte dataByte;

    public RadioPacket1BS(RawPacket rawPacket) {
        super(rawPacket);
    }

    @Override
    public void parseData() {
        super.parseData();
        dataByte = payload.getData()[1];
    }

    @Override
    protected void fillData() {
        super.fillData();
        ByteArray wrapper = new ByteArray();
        wrapper.addByte(dataByte);
        wrapper.addBytes(payload.getData());
    }

    public byte getDataByte() {
        return dataByte;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(", [dataByte=%02X", dataByte);
    }

}
