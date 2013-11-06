package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import java.util.logging.Logger;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader.PacketType;

public class ESP3TelegramRADIO extends ESP3Telegram /*implements ESP3TelegramInterface*/ {
	public static final Logger ESP3Logger = Logger.getLogger(EnoceanConnector.class.getName());

	/*
	byte[] data;

	private int dataLen;
	private int optDataLen;
	*/
	PacketType packetType;
	
	private int SubTelNum;
	private DeviceID DestinationID;
	private int dBm;
	private int SecurityLevel;
	
	private RORG rorg;

	private DeviceID senderID;
	private byte status;
	
	public ESP3TelegramRADIO(byte[] data, int dataLen, int optDataLen) {
		/*
		this.data = data;
		this.dataLen = dataLen;
		this.optDataLen = optDataLen;
		*/
		super(data, dataLen, optDataLen);
		this.senderID = extractSenderID();
		this.rorg = getRORGFromDataGroup (data);
		this.SubTelNum = extractSubTelNum();
		this.DestinationID = extractDestinationID();
		this.dBm = extractdBm();
		this.SecurityLevel = extractSecurityLevel();
		this.status = extractStatusByte();
	}

	public ESP3TelegramRADIO (byte[] telegramData)
	{
		// this.telegramData = telegramData;
		super(telegramData);
	}
	
	public ESP3TelegramRADIO (byte telegramData)
	{
		super(telegramData);
	}
	
	public ESP3TelegramRADIO(byte[] data, int dataLen, int optDataLen, PacketType packetType) {
		super(data, dataLen, optDataLen);
		this.packetType = packetType;
	}
	
	public ESP3TelegramRADIO ()
	{
		
	}
	
	public RORG getRORGFromDataGroup (byte[] data) 
	{
		if (data == null)
			return null;

		return RORG.checkValue(data[ESP3Constants.RORG_POS] & 0xFF);
	}

	public DeviceID extractSenderID() 
	{
		return null;
	}
	
	public byte extractStatusByte()
	{
		return 0;
	}
	
	private DeviceID extractDestinationID()
	{
		byte[] senderIDBytes = new byte[DeviceID.ID_LENGTH];
		System.arraycopy (data, dataLen + 1, senderIDBytes, 0, DeviceID.ID_LENGTH);

		return DeviceID.fromByteArray(senderIDBytes);		
	}
	
	private int extractdBm(){
		return (byte)(getOptionalPayload()[5] & 0xFF);
	}
	
	private int extractSubTelNum()
	{	
		return (byte)(getOptionalPayload()[0] & 0xFF);
	}
	
	private int extractSecurityLevel()
	{
		return (byte)(getOptionalPayload()[6] & 0xFF);
	}
	
	public int getdBm(){
		return dBm;
	}
	
	public byte getStatusByte() {
		return status;
	}
	
	public RORG getRORG() {

		return rorg;
	}

	public DeviceID getSenderID() {
		return senderID;
	}

	public byte[] getPayload() {
		return null;
	}

	public int getDataLen() {

		return dataLen;
	}

	public String getPayloadAsString()
	{
		return null;
	}

	public int getOptDataLen() {

		return optDataLen;
	}

	public byte[] getOptionalPayload()
	{
		byte[] optionalPayload = new byte[getOptDataLen()];

		System.arraycopy (data, getDataLen(), optionalPayload, 0, getOptDataLen());
		return optionalPayload;
	}

	public String getOptionalPayloadAsString()
	{
		StringBuilder builder = new StringBuilder();

		byte[] optionalPayload = getOptionalPayload(); 
		
		for (int i = 0; i < optionalPayload.length; i++)
		{
			builder.append(String.format("0x%02X ", optionalPayload[i]));
		}		
		
		return builder.toString();
	}
	
	public int getSubTelNum()
	{
		return SubTelNum;
	}
	
	public DeviceID getDestinationID(){
		return DestinationID;
	}
		
	public int getSecurityLevel()
	{
		return SecurityLevel;
	}
	
	public void setRORG (RORG val)
	{
		this.rorg = val;
	}
		
	public void setSenderID (DeviceID val)
	{
		this.senderID = val;
	}
	
	public void setSenderID (long val)
	{
		this.senderID = new DeviceID (val);
	}
	
	public void setSenderID (String val)
	{
		this.senderID = DeviceID.fromString(val);
	}
	
	public void setTelegramData (byte[] val)
	{
		this.telegramData = val;
	}
	
	public void setTelegramData (byte val)
	{
		byte[] tmp = new byte[1];
		tmp[0] = val;
		this.telegramData = tmp;
	}
	
	public void setStatus (byte val)
	{
		this.status = val;
	}
	
	public void setSecurityLevel (int val)
	{
		this.SecurityLevel = val;
	}
	
	public void setdBm (int val)
	{
		this.dBm = val;
	}
	
	public void setDestinationID (long val)
	{
		this.DestinationID = new DeviceID(val);
	}
	
	public void setDestinationID (DeviceID val)
	{
		this.DestinationID = val;
	}
	
	public void setDestionationID (String val)
	{
		this.DestinationID = DeviceID.fromString(val);
	}
	
	public void setSubTelNum (int val)
	{
		this.SubTelNum = val;
	}
	
	public byte[] toByteArray() {
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
	}
}
