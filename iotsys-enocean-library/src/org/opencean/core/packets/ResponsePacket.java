package org.opencean.core.packets;

import org.opencean.core.utils.ByteArray;

public class ResponsePacket extends BasicPacket {

    public static final byte PACKET_TYPE = 0x02;

    private byte returnCode;

    public ResponsePacket(RawPacket rawPacket) {
        super(rawPacket);
    }

    public ResponsePacket() {
        header.setPacketType(PACKET_TYPE);
    }

    public ResponsePacket(byte returnCode) {
        this.returnCode = returnCode;
    }

    public byte getReturnCode() {
        return returnCode;
    }

    protected byte[] getResponseData() {
        return new byte[] {};
    }

    @Override
    protected void fillData() {
        ByteArray data = new ByteArray();
        data.addByte(getReturnCode());
        data.addBytes(getResponseData());
        payload.setData(data.getArray());
    }

    @Override
    protected void parseData() {
        returnCode = payload.getData()[0];
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[returnCode=" + returnCode + "]";
    }

}
