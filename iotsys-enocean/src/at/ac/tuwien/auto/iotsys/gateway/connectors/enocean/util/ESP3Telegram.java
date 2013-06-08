package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3Telegram
{
    public enum RORG
    {
        RPS(0xF6),
        OneBS(0xD5),
        FourBS(0xA5),
        VLD(0xD2),
        MSC(0xD1),
        ADT(0xA6),
        SM_LRN_REQ(0xC6),
        SM_LRN_ANS(0xC7),
        SM_REC(0xA7),
        SYS_EX(0xC5);

        private byte value;

        private RORG (int value)
        {
            this.value = (byte)(value & 0xFF);
        }

        public byte getValue ()
        {
            return value;
        }
        
        public static RORG checkValue(int value)
        {
            RORG[] allTypes = RORG.values();
            byte rorgByte = (byte)(value & 0xFF);

            for (RORG rorgType : allTypes)
            {
                if ( rorgType.value == rorgByte )
                {
                    return rorgType;
                }
            }
            return null;
        }
    }
    
    public static final int ESP_RORG_LENGTH = 0x01;
    public static final int ESP_STATUS_LENGTH = 0x01;

    public static final int ESP3_RORG_POS = 0x00;

    byte[] data;
    private RORG rorg;
    private int dataLen;
    private int optDataLen;
    private DeviceID senderID;
    
    public ESP3Telegram (byte[] data, int dataLen, int optDataLen)
    {
    	this.data = data;
        this.dataLen = dataLen;
        this.optDataLen = optDataLen;
        this.senderID = getSenderIDFromDataGroup();
        this.rorg = getRORGFromDataGroup (data);
        
    }

    public static RORG getRORGFromDataGroup (byte[] data)
    {
        if (data == null)
            return null;

        return RORG.checkValue(data[ESP3_RORG_POS] & 0xFF);
    }
    
    private DeviceID getSenderIDFromDataGroup ()
    {
        byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
        System.arraycopy (data, ESP_RORG_LENGTH + 1, senderIDBytes, 0, DeviceID.ID_LENGTH);

        return DeviceID.fromByteArray(senderIDBytes);
    }

    public RORG getRORG()
    {
        return rorg;
    }

    public DeviceID getSenderID()
    {
        return senderID;
    }

    public byte[] getPayload()
    {
        byte[] payload = new byte[1];

        System.arraycopy (data, ESP_RORG_LENGTH, payload, 0, 1);
        return payload;
    }

    public String getPayloadAsString()
    {
    	StringBuilder builder = new StringBuilder();
    	
    	byte[] payload = getPayload(); 
    	
    	for (int i = 0; i < payload.length; i++)
    	{
    		builder.append(String.format("0x%02X", payload[i]));
    	}
    	
    	return builder.toString();
    }
    
    byte getStatusByte()
    {
        return data[ESP_RORG_LENGTH + 1 + DeviceID.ID_LENGTH];
    }

}
