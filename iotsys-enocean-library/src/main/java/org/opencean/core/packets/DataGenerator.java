package org.opencean.core.packets;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.utils.ByteArray;

public class DataGenerator {
	private ByteArray data = new ByteArray();
	
	public DataGenerator(byte type, byte state, EnoceanParameterAddress deviceID, byte status){
		data.addByte(type);
		data.addByte(state);
		data.addBytes(deviceID.getEnoceanDeviceId().toBytes());
		data.addByte(status);
	}
	
	public byte[] getData(){
		return data.getArray();
	}
}
