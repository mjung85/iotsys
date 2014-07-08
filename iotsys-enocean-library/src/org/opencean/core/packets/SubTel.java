package org.opencean.core.packets;

public class SubTel {

    private byte tick;

    private byte dBm;

    private byte status;

    public byte getTick() {
        return tick;
    }

    public void setTick(byte tick) {
        this.tick = tick;
    }

    public byte getdBm() {
        return dBm;
    }

    public void setdBm(byte dBm) {
        this.dBm = dBm;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
}
