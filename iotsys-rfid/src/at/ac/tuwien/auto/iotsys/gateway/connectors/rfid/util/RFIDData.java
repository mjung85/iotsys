package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util;

public class RFIDData {


	private byte[] data;
	
	public RFIDData (byte[] data)
	{
		this.data = data;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
	}
}
