package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3TelegramRPS extends ESP3TelegramRADIO {
	
	public ESP3TelegramRPS (byte[] data, int dataLen, int optDataLen) {
		super(data, dataLen, optDataLen);
	}
	
	public ESP3TelegramRPS (byte telegramData)
	{
		super(telegramData);
	}

	@Override
	public byte extractStatusByte() {
		return data[ESP3Constants.RORG_LENGTH + 1 + DeviceID.ID_LENGTH];
	}

	@Override
	public DeviceID extractSenderID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, ESP3Constants.RORG_LENGTH + 1, senderIDBytes, 0, DeviceID.ID_LENGTH);

		return DeviceID.fromByteArray(senderIDBytes);
	}

	@Override
	public byte[] getPayload()
	{
		byte[] payload = new byte[1];
		System.arraycopy (data, ESP3Constants.RORG_LENGTH, payload, 0, 1);
		return payload;
	}
	
	@Override
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
	
	/*
	public byte[] toByteArray()
	{
		
		return data;
		
	}
	*/
}
