package org.opencean.core.packets;

import org.opencean.core.utils.ByteArray;

public abstract class BasicPacket {

    /*
     * More packet types:
     * 
     * 0: Reserved
     * 
     * 8..127: Reserved for EnOcean
     * 
     * 128..255: Manufacturer specific commands and messages
     */

    public static final int HEADER_LENGTH = 4;

    /**
     * Radio subtelegram
     */
    public static final byte PACKET_TYPE_RADIO_SUB_TEL = 0x03;

    /**
     * Event message
     */
    public static final byte PACKET_TYPE_EVENT = 0x04;
    public static final byte PACKET_TYPE_COMMON_COMMAND = 0x05;
    public static final byte PACKET_TYPE_SMART_ACK_COMMAND = 0x06;

    /**
     * Remote management command
     */
    public static final int POS_HEADER_START = 1;
    public static final int POS_DATA_START = 6;
    public static final byte SYNC_BYTE = 0x55;

    protected Header header;
    protected Payload payload;

    public BasicPacket(RawPacket rawPacket) {
        header = rawPacket.getHeader();
        payload = rawPacket.getPayload();
        parseData();
        parseOptionalData();
    }

    public BasicPacket() {
        header = new Header();
        payload = new Payload();
    }

    protected void fillHeader() {
        header.setDataLength((short) payload.getData().length);
        header.setOptionalDataLength((byte) payload.getOptionalData().length);
        header.initCRC8();
    }

    protected void fillPayload() {
        fillData();
        fillOptionalData();
        payload.initCRC8();
    }

    protected void fillData() {
        payload.setData(new byte[] {});
    }

    protected void fillOptionalData() {
        payload.setOptionalData(new byte[] {});
    }

    protected void parseData() {
    }

    protected void parseOptionalData() {
    }

    public byte[] toBytes() {
        fillPayload();
        fillHeader();
        ByteArray message = new ByteArray();
        message.addByte(SYNC_BYTE);
        message.addBytes(header.toBytes());
        message.addBytes(payload.toBytes());
        return message.getArray();
    }

    public byte getPacketType() {
        return header.getPacketType();
    }

    public int getDataLength() {
        return header.getDataLength();
    }

    public byte getOptionalDataLength() {
        return header.getOptionalDataLength();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[header=" + header + ", payload=" + payload + "], raw=" + new ByteArray(toBytes());
    }
}
