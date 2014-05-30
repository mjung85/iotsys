package org.opencean.core.common.values;

public class ByteStateAndStatus {

    public static final byte ON = (byte)0x10;
    public static final byte OFF = (byte)0x30;
    public static final byte PRESSED = (byte)0x30;
    public static final byte RELEASED = (byte)0x20;
    public static final byte ERROR = (byte)0xFE;
    
    public static byte getByteFor(String stateOrStatus){
    	if (stateOrStatus.equals("ON"))
    		return ON;
    	else if (stateOrStatus.equals("OFF"))
    		return OFF;
    	return ERROR;
    }

}
