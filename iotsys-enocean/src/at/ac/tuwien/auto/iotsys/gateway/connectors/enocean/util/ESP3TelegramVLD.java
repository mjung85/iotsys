package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3TelegramVLD extends ESP3TelegramRADIO {


	public ESP3TelegramVLD(byte[] data, int dataLen, int optDataLen) {
		super(data, dataLen, optDataLen);
	}

	public ESP3TelegramVLD (byte[] data)
	{
		super(data);
	}
	
	@Override
	public byte extractStatusByte() {
		return data[getDataLen() - 2];
	}
	
	public byte getCRC8Byte ()
	{
		return data[getDataLen() - 1];
	}
	
	@Override
	public DeviceID extractSenderID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, getDataLen() - 6, senderIDBytes, 0, DeviceID.ID_LENGTH);

		return DeviceID.fromByteArray(senderIDBytes);
	}

	@Override
	public byte[] getPayload()
	{
		byte[] payload = new byte[1];

		System.arraycopy (data, ESP3Constants.RORG_LENGTH, payload, 0, getDataLen() - 7);
		return payload;
	}
}
