package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class ESP3Packet
{
	private ESP3PacketHeader header;
	private byte[] data;
	private byte[] optData;
	private byte[] allData;
	public ESP3Telegram telegram;
	
	public ESP3Packet (ESP3PacketHeader header, byte[] allData)
	{
		this.allData = allData;
		this.header = header;
		this.data = new byte[header.getDataLength()];
		this.optData = new byte[header.getOptionalDataLength()];
		this.telegram = new ESP3Telegram(allData, header.getDataLength(), header.getOptionalDataLength());
		System.arraycopy (allData, 0, data, 0, header.getDataLength());
		System.arraycopy (allData, header.getDataLength(), optData, 0, header.getOptionalDataLength());
	}
	
    public ESP3PacketHeader.PacketType getPacketType ()
    {
        return header.getPacketType ();
    }

    public byte[] getData()
    {
        if (data == null)
            return new byte[] {};

        return data.clone();
    }

    public byte[] getOptionalData()
    {
        if (optData == null)
            return new byte[] {};

        return optData.clone();
    }
    
    public ESP3PacketHeader getHeader()
    {
    	return header;
    }
}
