package org.opencean.core.packets;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.utils.Bits;
import org.opencean.core.utils.ByteArray;

public class RadioPacket4BS extends RadioPacket {

    public static final byte RADIO_TYPE = (byte) 0xA5;

    public static final int DATA_LENGTH = 10;
    public static final byte OPT_DATA_LENGTH = 0x07;

    private byte db0;
    private byte db1;
    private byte db2;
    private byte db3;
    
    private byte rorg;
    private EnoceanId senderId;
    private byte status;
    
    private byte subTelNum;
    private EnoceanId destinationId;
    private byte dBm;
    private byte securityLevel;

    public RadioPacket4BS() {
    }

    public RadioPacket4BS(RawPacket rawPacket) {
        super(rawPacket);
    }
    
    public RadioPacket4BS(byte[] data, int senderId, byte status, byte subTelNum, int destinationId, byte dBm, byte securityLevel) {
    	this(data, senderId, status);
    	this.subTelNum = subTelNum;
        this.destinationId = EnoceanId.fromInt(destinationId);
        this.dBm = dBm;
        this.securityLevel = securityLevel;       
        ByteArray wrapper = new ByteArray();
        wrapper.addByte(this.subTelNum);
        wrapper.addBytes(this.destinationId.toBytes());
        wrapper.addByte(this.dBm);
        wrapper.addByte(this.securityLevel);
        payload.setOptionalData(wrapper.getArray());
        payload.initCRC8();    	    	
    }
    
    
    public RadioPacket4BS(byte[] data, int senderId, byte status) {
    	header.setPacketType(PACKET_TYPE);
    	header.setDataLength((short)DATA_LENGTH);
    	header.setOptionalDataLength(OPT_DATA_LENGTH);
    	header.initCRC8();
    	this.rorg = RADIO_TYPE;
        this.db3 = data[3];
        this.db2 = data[2];
        this.db1 = data[1];
        this.db0 = data[0];
        this.senderId = EnoceanId.fromInt(senderId);    
        this.status = status;
        //fillData();
        ByteArray wrapper = new ByteArray();
        wrapper.addByte(rorg);
        wrapper.addByte(db3);
        wrapper.addByte(db2);
        wrapper.addByte(db1);
        wrapper.addByte(db0);
        wrapper.addBytes(this.senderId.toBytes());
        wrapper.addByte(this.status);        
        payload.setData(wrapper.getArray());
        payload.initCRC8();
    }
        
    @Override
    protected void fillData() {
        // nothing to do, data already set
    }
    
    @Override
    protected void fillOptionalData() {
    	// nothing to do, data already set
    }

    @Override
    protected void parseData() {
        super.parseData();
        byte[] data = payload.getData();
        db3 = data[1];
        db2 = data[2];
        db1 = data[3];
        db0 = data[4];
    }

    public byte getDb0() {
        return db0;
    }

    public byte getDb1() {
        return db1;
    }

    public byte getDb2() {
        return db2;
    }

    public byte getDb3() {
        return db3;
    }

    public void setDb1(byte db1) {
        this.db1 = db1;
    }

    public boolean isTeachInMode() {
        return !(Bits.isBitSet(db0, 3));
    }

    @Override
    public String toString() {
        return super.toString()
                + String.format(", [db0=%02X, db1=%02X, db2=%02X, db3=%02X, teachIn=%s]", db0, db1, db2, db3, isTeachInMode());
    }
}
