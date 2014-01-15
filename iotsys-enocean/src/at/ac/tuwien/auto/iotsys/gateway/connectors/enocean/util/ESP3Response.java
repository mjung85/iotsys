package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3Response extends ESP3Telegram {

	public enum ResponseReturnCode
	{
		RET_OK(0x01),
		RET_ERROR(0x02),
		RET_NOT_SUPPORTED(0x03),
		RET_WRONG_PARAM(0x04),
		RET_OPERATION_DENIED(0x05);
		
		private byte value;
		
		public int getValue()
		{
			return value;
		}
		
		private ResponseReturnCode (int value)
		{
			this.value = (byte)(value & 0xFF);
		}
		
		public static ResponseReturnCode checkValue(int value)
		{
			ResponseReturnCode []allTypes = ResponseReturnCode.values();
			byte codeByte = (byte)(value & 0xFF);
			for (ResponseReturnCode returnType : allTypes)
			{
				if (returnType.value == codeByte )
				{
					return returnType;
				}
			}
			
			return null;
		}
	}
	
	public ESP3Response() {

	}
	
	public ESP3Response (byte[] data, int dataLen, int optDataLen)
	{
		super(data, dataLen, optDataLen);
	}
	
	byte[] getData(){
		return data;
	}
	
	public byte[] getPayload()
	{
		byte[] payload = new byte[getDataLen()];
		System.arraycopy (data, 0, payload, 0, getDataLen());
		return payload;
	}
	
	public String getPayloadAsString()
	{
		StringBuilder builder = new StringBuilder();
		
		byte[] payload = getPayload(); 

		for (int i = 0; i < payload.length; i++)
		{
			builder.append(String.format("0x%02X ", payload[i]));
		}

		return builder.toString();
	}
}
