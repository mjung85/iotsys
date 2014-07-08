package org.opencean.core.packets;

import java.nio.ByteBuffer;

import org.opencean.core.utils.ByteArray;

public class ConfirmLearnEventResponsePacket extends ResponsePacket {

    /**
     * Response time for Smart Ack Client in ms in which the controller can
     * prepare the data and send it to the postmaster. Only actual if learn
     * return code is Learn IN
     */
    private short responseTime;

    /**
     * @see LearnAckEventPacket
     */
    private byte confirmCode;

    public ConfirmLearnEventResponsePacket(byte confirmCode) {
        this.confirmCode = confirmCode;
        responseTime = (short) System.currentTimeMillis();
    }

    @Override
    protected void fillData() {
        ByteArray wrapper = new ByteArray();
        wrapper.addShort(responseTime);
        wrapper.addByte(confirmCode);
        payload.setData(wrapper.getArray());
    }

    @Override
    protected void parseData() {
        super.parseData();
        ByteBuffer bb = ByteBuffer.wrap(payload.getData());
        responseTime = bb.getShort();
        confirmCode = bb.get();
    }

    public short getResponseTime() {
        return responseTime;
    }

    public byte getConfirmCode() {
        return confirmCode;
    }

}
