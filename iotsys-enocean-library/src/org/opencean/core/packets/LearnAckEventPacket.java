package org.opencean.core.packets;

import java.nio.ByteBuffer;

import org.opencean.core.utils.ByteArray;

/**
 * Informs Smart Ack client about the result of a previous sent learn request.
 * 
 * @author thomas
 * 
 */
public class LearnAckEventPacket extends EventPacket {

    public static final byte CONFIRM_CODE_FUNCTION_NOT_SUPPORTED = (byte) 0xFF;

    public static final byte CONFIRM_CODE_LEARN_OUT = 0x20;

    /**
     * Discard Learn IN, RSSI was not good enough
     */
    public static final byte CONFIRM_CODE_RSSI_NOT_ENOUGH = 0x14;

    /**
     * Discard Learn IN, Controller has no place for new sensor
     */
    public static final byte CONFIRM_CODE_NO_SENSOR_PLACE = 0x13;

    /**
     * Discard Learn IN, PM has no place for further mailbox
     */
    public static final byte CONFIRM_CODE_NO_MAILBOX_PLACE = 0x12;

    /**
     * Discard Learn IN, EEP not accepted
     */
    public static final byte CONFIRM_CODE_EEP_NOT_ACCEPTED = 0x11;

    public static final byte CONFIRM_CODE_LEARN_IN = 0x00;

    /**
     * Response time for Smart Ack Client in ms in which the controller can
     * prepare the data and send it to the postmaster. Only actual if learn
     * return code is Learn IN
     */
    private short responseTime;

    private byte confirmCode;

    public LearnAckEventPacket(byte confirmCode) {
        this.confirmCode = confirmCode;
        responseTime = (short) System.currentTimeMillis();
    }

    @Override
    public byte[] getEventData() {
        ByteArray wrapper = new ByteArray();
        wrapper.addShort(responseTime);
        wrapper.addByte(confirmCode);
        return wrapper.getArray();
    }

    @Override
    protected void parseData() {
        super.parseData();
        ByteBuffer bb = ByteBuffer.wrap(payload.getData());
        responseTime = bb.getShort();
        confirmCode = bb.get();
    }

    @Override
    public boolean isEventCodeSupported() {
        return getEventCode() == EVENT_CODE_SA_LEARN_ACK;
    }

    public short getResponseTime() {
        return responseTime;
    }

    public byte getConfirmCode() {
        return confirmCode;
    }

}
