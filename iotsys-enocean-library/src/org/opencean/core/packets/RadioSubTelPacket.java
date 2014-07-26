package org.opencean.core.packets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.opencean.core.utils.ByteArray;

public class RadioSubTelPacket extends RadioPacket {

    private short timeStamp;

    private List<SubTel> subTels;

    public RadioSubTelPacket(byte subTelNum, int destinationId, byte dBm, byte securityLevel, List<SubTel> subTels) {
        super(subTelNum, destinationId, dBm, securityLevel);
        this.subTels = subTels;
        timeStamp = (short) (System.currentTimeMillis() & 0xFFFF);
    }

    public List<SubTel> getSubTels() {
        return subTels;
    }

    public void setSubTels(List<SubTel> subTels) {
        this.subTels = subTels;
    }

    @Override
    protected void fillOptionalData() {
        super.fillOptionalData();
        ByteArray optional = new ByteArray(payload.getOptionalData());
        optional.addShort(timeStamp);
        for (SubTel subTel : subTels) {
            optional.addByte(subTel.getTick());
            optional.addByte(subTel.getdBm());
            optional.addByte(subTel.getStatus());
        }
        payload.setOptionalData(optional.getArray());
    }

    @Override
    protected void parseOptionalData() {
        super.parseOptionalData();
        ByteBuffer optionalDataBytes = ByteBuffer.wrap(payload.getOptionalData());
        optionalDataBytes.position(7);
        timeStamp = optionalDataBytes.getShort();
        subTels = new ArrayList<SubTel>();
        while (optionalDataBytes.hasRemaining()) {
            SubTel subTel = new SubTel();
            subTel.setTick(optionalDataBytes.get());
            subTel.setdBm(optionalDataBytes.get());
            subTel.setStatus(optionalDataBytes.get());
        }
    }
}
