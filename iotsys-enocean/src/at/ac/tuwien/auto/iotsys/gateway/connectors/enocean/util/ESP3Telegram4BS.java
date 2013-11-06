package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3Telegram4BS extends ESP3TelegramRADIO {


	public ESP3Telegram4BS(byte[] data, int dataLen, int optDataLen) {
		super(data, dataLen, optDataLen);
	}

	public ESP3Telegram4BS (byte[] data)
	{
		super(data);
	}
	
	@Override
	public DeviceID extractSenderID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, ESP3Constants.RORG_LENGTH + 4, senderIDBytes, 0, DeviceID.ID_LENGTH);

		return DeviceID.fromByteArray(senderIDBytes);
	}
	
	@Override
	public byte extractStatusByte() {
		return data[ESP3Constants.RORG_LENGTH + 4 + DeviceID.ID_LENGTH];
	}

	@Override
	public byte[] getPayload()
	{
		byte[] payload = new byte[1];

		System.arraycopy (data, ESP3Constants.RORG_LENGTH, payload, 0, 4);
		return payload;
	}
}
