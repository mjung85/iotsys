package org.opencean.core.packets;

import org.opencean.core.utils.ByteArray;

public class QueryIdCommand extends BasicPacket {

    public static final byte PACKET_TYPE_REMOTE_MAN_COMMAND = (byte) 0x07;
    public static final byte RADIO_TYPE_RMCC = (byte) 0xA6;
    public static final int DESTINATION_ID_MULTICAST = 0xFFFFFFFF;
    private static final int SOURCE_ID_SEND_CASE = 0x0000000;
    public static final byte FUNCTION_CODE = 0x04;
    public static final short DEFAULT_MANUFACTURER_ID = 0x07FF;

    public QueryIdCommand() {
    }

    @Override
    protected void fillHeader() {
        super.fillHeader();
        header.setPacketType(PACKET_TYPE_REMOTE_MAN_COMMAND);
    }

    @Override
    protected void fillData() {
        super.fillData();
        ByteArray wrapper = new ByteArray();
        wrapper.addShort(FUNCTION_CODE);
        wrapper.addShort(DEFAULT_MANUFACTURER_ID);
        wrapper.addInt(DESTINATION_ID_MULTICAST);
        wrapper.addInt(SOURCE_ID_SEND_CASE);
        payload.setData(wrapper.getArray());
    }
}
