package org.opencean.core.packets;

import java.nio.ByteBuffer;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.utils.ByteArray;

public class RadioPacket extends BasicPacket {

    public static final byte PACKET_TYPE = 0x01;

    private EnoceanId senderId;
    private int repeaterCount;
    private byte status;

    private byte subTelNum;
    private EnoceanId destinationId;
    private byte dBm;
    private byte securityLevel;

    public RadioPacket(RawPacket rawPacket) {
        super(rawPacket);
    }

    public RadioPacket() {
        header.setPacketType(PACKET_TYPE);
    }

    /**
     * @param subTelNum
     *            Number of subTelegram. Send = 3, receive = 1..x
     * @param destinationId
     *            Destination Id (4 byte). Broadcast Radio = FF FF FF FF, ADT
     *            radio: Destination ID (address)
     * @param dBm
     *            Send case: FF, Receive case: best RSSI value of all received
     *            subtelegrams (value decimal without minus)
     * @param securityLevel
     *            Security Level. 0 = unencrypted, x = type of encryption
     */
    public RadioPacket(byte[] data, byte subTelNum, int destinationId, byte dBm, byte securityLevel) {
        this(subTelNum, destinationId, dBm, securityLevel);
        payload.setData(data);
    }

    /**
     * @param subTelNum
     *            Number of subTelegram. Send = 3, receive = 1..x
     * @param destinationId
     *            Destination Id (4 byte). Broadcast Radio = FF FF FF FF, ADT
     *            radio: Destination ID (address)
     * @param dBm
     *            Send case: FF, Receive case: best RSSI value of all received
     *            subtelegrams (value decimal without minus)
     * @param securityLevel
     *            Security Level. 0 = unencrypted, x = type of encryption
     */
    protected RadioPacket(byte subTelNum, int destinationId, byte dBm, byte securityLevel) {
        this.subTelNum = subTelNum;
        this.destinationId = EnoceanId.fromInt(destinationId);
        this.dBm = dBm;
        this.securityLevel = securityLevel;
        header.setPacketType(PACKET_TYPE);
        fillOptionalData();
    }

    @Override
    protected void parseData() {
        byte[] data = payload.getData();
        int length = data.length;
        senderId = EnoceanId.fromByteArray(data, length - 5);
        status = data[length - 1];
        repeaterCount = (status & 0x0F);
    }

    @Override
    protected void fillData() {
        // nothing to do, data already set
    }
    
    @Override
    protected void fillOptionalData() {
        ByteArray wrapper = new ByteArray();
        wrapper.addByte(subTelNum);
        wrapper.addBytes(destinationId.toBytes());
        wrapper.addByte(dBm);
        wrapper.addByte(securityLevel);
        payload.setOptionalData(wrapper.getArray());
    }

    @Override
    protected void parseOptionalData() {
        if (header.getOptionalDataLength() == 0) {
            return;
        }
        ByteBuffer optionalDataBytes = ByteBuffer.wrap(payload.getOptionalData());
        subTelNum = optionalDataBytes.get();
        destinationId = EnoceanId.fromInt(optionalDataBytes.getInt());
        dBm = optionalDataBytes.get();
        securityLevel = optionalDataBytes.get();
    }

    public EnoceanId getSenderId() {
        return senderId;
    }

    public byte getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return super.toString() + ", [sender=" + senderId + ", repeaterCount=" + repeaterCount + "]";
    }

}
