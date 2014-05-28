package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader.PacketType;

public class ESP3TelegramRemoteManCmd extends ESP3Telegram {

	private int functionNo;
	private int manufacturerID;
	
	private DeviceID destinationID;
	private DeviceID sourceID;
    private int dBm;	
	
	byte[] messageData;
	private int messageDataLen;

	private int sendWithDelay;
	
	public ESP3TelegramRemoteManCmd(byte[] data, int dataLen, int optDataLen, PacketType packetType) {
		super(data, dataLen, optDataLen, packetType);
		this.functionNo = (((data[0] & 0xFF) << 8) + (data[1] & 0xFF));
		this.manufacturerID = (((data[2] & 0xFF) << 8) + (data[3] & 0xFF));
		
		this.messageDataLen = dataLen - 4;
		messageData = new byte[messageDataLen];
		System.arraycopy(data, 4, messageData, 0, messageDataLen);
		
		this.destinationID = extractDestinationID();
		this.sourceID = extractSourceID();
		this.dBm = extractdBm();
		this.sendWithDelay = (byte)(data[dataLen + optDataLen - 1] & 0xFF);
	}

	public ESP3TelegramRemoteManCmd (byte[] data)
	{
		super(data);
	}
	
	public ESP3TelegramRemoteManCmd() {
		
	}

	public ESP3TelegramRemoteManCmd(byte data) {
		super(data);
	}

	public int getFunctionNo ()
	{
		return functionNo;
	}
	
	public void setFunctionNo (int val)
	{
		this.functionNo = val;
	}
	
	public int getManufacturerID ()
	{
		return manufacturerID;
	}
	
	public void setManufacturerID (int val)
	{
		this.manufacturerID = val;
	}
	
	public byte[] getMessageData ()
	{
		return messageData;
	}
	
	public void setMessageData (byte[] data)
	{
		this.messageData = data;
	}
	
	public int getSendWithDelay ()
	{
		return this.sendWithDelay;
	}
	
	public void setSendWithDelay (int val)
	{
		this.sendWithDelay = val;
	}

	public int getDataLen ()
	{
		return dataLen;
	}
	
	public int getOptDataLen ()
	{
		return optDataLen;
	}
	
	private DeviceID extractDestinationID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, dataLen, senderIDBytes, 0, DeviceID.ID_LENGTH);
		
		return DeviceID.fromByteArray(senderIDBytes);		
	}
	
	public byte[] getOptionalPayload()
	{
		byte[] optionalPayload = new byte[getOptDataLen()];

		System.arraycopy (data, getDataLen(), optionalPayload, 0, getOptDataLen());
		return optionalPayload;
	}
	
	private int extractdBm() {
		return (byte)(getOptionalPayload()[8] & 0xFF);
	}

	public DeviceID extractSourceID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, dataLen + 4, senderIDBytes, 0, DeviceID.ID_LENGTH);

		return DeviceID.fromByteArray(senderIDBytes);
	}

	public DeviceID getSenderID()
	{
		return this.sourceID;
	}
	
	public DeviceID getDestinationID()
	{
		return this.destinationID;
	}
	
	@Override
	public byte[] getPayload()
	{
		byte[] payload = new byte[dataLen];
		System.arraycopy (data, ESP3Constants.RORG_LENGTH, payload, 0, dataLen);
		return payload;
	}
	
	public int getdBm()
	{
		return dBm;
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
	
	public byte[] toByteArray() {
		byte[] optData = new byte[10];
		System.arraycopy(getDestinationID().toByteArray(), 0, optData, 0, 4);
		System.arraycopy(getSenderID().toByteArray(), 0, optData, 4, 4);
		optData[8] = (byte)(getdBm() & 0xFF);
		optData[9] = (byte)this.sendWithDelay;
		optDataLen = optData.length;
		
		byte[] rawData = new byte[telegramData.length + 4];
		rawData[0] = (byte)((functionNo >> 8) & 0xFF);
		rawData[1] = (byte)(functionNo & 0xFF);
		rawData[2] = (byte)((manufacturerID >> 8) & 0xFF);
		rawData[3] = (byte)(manufacturerID & 0xFF);
		System.arraycopy(this.telegramData, 0, rawData, 4, this.telegramData.length);
		dataLen = rawData.length;
		
		byte[] allData = new byte[dataLen + optDataLen];
		System.arraycopy(rawData, 0, allData, 0, dataLen);
		System.arraycopy(optData, 0, allData, dataLen, optDataLen);
		
		return allData;
	}
}
