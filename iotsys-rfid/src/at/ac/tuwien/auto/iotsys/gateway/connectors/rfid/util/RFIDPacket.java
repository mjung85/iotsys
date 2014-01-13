package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util;

public class RFIDPacket {

	RFIDPacketHeader header;
	RFIDData data;
	
	public RFIDPacket (RFIDPacketHeader header, RFIDData data)
	{
		byte[] adata = new byte[header.getDataLength()];
		this.header = header;
		
		System.arraycopy (data.getData(), 0, adata, 0, header.getDataLength());
		data.setData(adata);
	}
	
	public RFIDData getData()
	{
		
		return this.data;
	}
	
	public RFIDPacketHeader getHeader()
	{
		
		return this.header;
	}
}
