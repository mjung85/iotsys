package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public interface ESP3TelegramInterface
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


    public RORG getRORG();
	public byte[] toByteArray();
}

