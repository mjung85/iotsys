package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3TelegramCommonCommand extends ESP3Telegram {

	public enum CommonCommandCodes
	{
		CO_WR_SLEEP(1),
		CO_WR_RESET(2),
		CO_RD_VERSION(3),
		CO_RD_SYS_LOG(4),
		CO_WR_SYS_LOG(5),
		CO_WR_BIST(6),
		CO_WR_IDBASE(7),
		CO_RD_IDBASE(8), 
		CO_WR_REPEATER(9),
		CO_RD_REPEATER(10),
		CO_WR_FILTER_ADD(11),
		CO_WR_FILTER_DEL(12),
		CO_WR_FILTER_DEL_ALL(13),
		CO_WR_FILTER_ENABLE(14),
		CO_RD_FILTER(15),
		CO_WR_WAIT_MATURITY(16),
		CO_WR_SUBTEL(17),
		CO_WR_MEM(18),
		CO_RD_MEM(19),
		CO_RD_MEM_ADDRESS(20),
		CO_RD_SECURITY(21),
		CO_WR_SECURITY(22),
		CO_WR_LEARNMODE(23),
		CO_RD_LEARNMODE(24),
		CO_WR_SECUREDEVICE_ADD(25),
		CO_WR_SECUREDEVICE_DEL(26),
		CO_RD_SECUREDEVICES(27);
		
		private byte value;
		
		private CommonCommandCodes (int value)
		{
			this.value = (byte)(value & 0xFF);
		}
		
		public int getValue()
		{
			return value;
		}
		
		public static CommonCommandCodes checkValue(int value)
		{
			CommonCommandCodes []allTypes = CommonCommandCodes.values();
			byte codeByte = (byte)(value & 0xFF);
			for (CommonCommandCodes commonCmdType : allTypes)
			{
				if (commonCmdType.value == codeByte )
				{
					return commonCmdType;
				}
			}
			
			return null;
		}
	}
	
	int cmdCode;
	byte[] cmdData;
	byte[] optionalData;
	
	public ESP3TelegramCommonCommand () 
	{	
	}
	
	public ESP3TelegramCommonCommand (byte[] data, int dataLen, int optDataLen) 
	{
		super(data, dataLen, optDataLen);
	}
	
	public void setData(byte[] data)
	{
		
	}
	
	@Override
	public byte[] toByteArray() {
		return data;
		/*
		byte[] optData = new byte[7];
		optData[0] = (byte)this.SubTelNum;
		System.arraycopy(DestinationID.toByteArray(), 0, optData, 1, 4);
		optData[5] = (byte)this.dBm;
		optData[6] = (byte)this.SecurityLevel;
		this.optDataLen = optData.length;
		
		byte[] rawData = new byte[telegramData.length + 1 + 4 + 1];
		rawData[0] = this.rorg.getValue();
		System.arraycopy(this.telegramData, 0, rawData, 1, this.telegramData.length);
		System.arraycopy(senderID.toByteArray(), 0, rawData, telegramData.length + 1, DeviceID.ID_LENGTH);
		rawData[telegramData.length + DeviceID.ID_LENGTH + 1] = this.status;
		this.dataLen = rawData.length;
		
		byte[] allData = new byte[dataLen + optDataLen];
		System.arraycopy(rawData, 0, allData, 0, dataLen);
		System.arraycopy(optData, 0, allData, dataLen, optDataLen);
		
		return allData;
		*/
	}
}
