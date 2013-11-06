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
		
		switch (header.getPacketType()){
		case RADIO:
			    this.telegram = new ESP3TelegramRADIO(allData, header.getDataLength(), header.getOptionalDataLength());
				switch (telegram.getRORG()) {
					case RPS:
						this.telegram = new ESP3TelegramRPS(allData, header.getDataLength(), header.getOptionalDataLength());
					break;
					case OneBS:
						this.telegram = new ESP3TelegramOneBS(allData, header.getDataLength(), header.getOptionalDataLength());
						break;
					case FourBS:
						this.telegram = new ESP3Telegram4BS(allData, header.getDataLength(), header.getOptionalDataLength());
					break;
					case VLD:
						this.telegram = new ESP3TelegramVLD(allData, header.getDataLength(), header.getOptionalDataLength());
					break;
		
					default:
						
					break;
				}
				break;
		case RESPONSE:
			// TODO : make response pkg tracing ...
			this.telegram = new ESP3Response(allData, header.getDataLength(), header.getOptionalDataLength());
			System.out.println("Response packet received: " + telegram.getPayloadAsString());
			break;
		case REMOTE_MAN_COMMAND:
				this.telegram = new ESP3TelegramRemoteManCmd(allData, header.getDataLength(), header.getOptionalDataLength(), header.getPacketType());
			break;
			default:
				break;
		}
		
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
