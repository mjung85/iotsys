package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util;

public class RFIDPacketHeader {

	private byte stx;
	private byte station_id;
	private byte data_len;
	
	
	public RFIDPacketHeader ()
	{
		
	}
	
	public void setSTX(byte stx) 
	{
		this.stx = stx;
	}
	
	public void setStationID(byte station_id)
	{
		this.station_id = station_id;
	}
	
	public void setDataLength(byte data_len)
	{
		this.data_len = data_len;
	}
	
	public byte getSTX()
	{
		return this.stx;
	}
	
	public byte getStationID()
	{
		return this.station_id;
	}

	public byte getDataLength()
	{
		return this.data_len;
	}
}
