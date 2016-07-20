package org.opencean.core.packets;

import java.nio.ByteBuffer;

import org.opencean.core.utils.ByteArray;

public class ADTRadioPacket extends RadioPacket {

    private byte[] userData;

    private int senderId;

    private byte status;

    private byte radioType;

    /**
     * @param radioType
     *            Radio type, e.g. VLD = D2, 4BS = 0xA5
     * @param userData
     *            1..9 byte data payload
     * @param senderId
     *            Unique device sender Id
     * @param status
     *            Telegram control bits â used in case of repeating, switch
     *            telegram encapsulation, checksum type identification
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
    public ADTRadioPacket(byte radioType, byte[] userData, int senderId, byte status, byte subTelNum, int destinationId, byte dBm,
            byte securityLevel) {
        super(subTelNum, destinationId, dBm, securityLevel);
        this.radioType = radioType;
        this.userData = userData;
        this.senderId = senderId;
        this.status = status;
    }

    @Override
    protected void fillData() {
        super.fillData();
        ByteArray data = new ByteArray();
        data.addByte(radioType);
        data.addBytes(userData);
        data.addInt(senderId);
        data.addByte(status);
        payload.setData(data.getArray());
    }

    @Override
    protected void parseData() {
        super.parseData();
        // TODO: rework!!!
        ByteBuffer bb = ByteBuffer.wrap(payload.getData());
        radioType = bb.get();
        bb.get(userData, 1, getDataLength() - 3);
        senderId = bb.get();
        status = bb.get();
    }

}
