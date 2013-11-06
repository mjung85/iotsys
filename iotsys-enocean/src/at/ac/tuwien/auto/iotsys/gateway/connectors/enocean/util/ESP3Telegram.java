package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.EnoceanConnector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader.PacketType;

public abstract class ESP3Telegram implements ESP3TelegramInterface {

	public static final Logger ESP3Logger = Logger.getLogger(EnoceanConnector.class.getName());

	byte[] data;

	int dataLen;
	int optDataLen;
	byte[] telegramData;
	
	public ESP3Telegram (byte[] data, int dataLen, int optDataLen) {
		this.data = data;
		this.dataLen = dataLen;
		this.optDataLen = optDataLen;
	}

	public ESP3Telegram (byte[] telegramData)
	{
		this.telegramData = telegramData;
	}
	
	public ESP3Telegram (byte telegramData)
	{
		byte[] val = new byte[1];
		val[0] = telegramData;
		this.telegramData = val;
	}
	
	public ESP3Telegram (byte[] data, int dataLen, int optDataLen, PacketType packetType) {
		this.data = data;
		this.dataLen = dataLen;
		this.optDataLen = optDataLen;

	}
	
	public ESP3Telegram ()
	{
		
	}

	public int getDataLen()
	{
		return this.dataLen;
	}
	
	public int getOptDataLen()
	{
		return this.optDataLen;
	}
	
	public byte[] getPayload()
	{
		return new byte[] {};
	}
	
	public String getPayloadAsString()
	{
		return "";
	}
	
	@Override
	public byte[] toByteArray() {
		return null;
	}

	@Override
	public RORG getRORG() {
		// TODO Auto-generated method stub
		return null;
	}

	public DeviceID getSenderID()
	{
		return null;
	}
}
