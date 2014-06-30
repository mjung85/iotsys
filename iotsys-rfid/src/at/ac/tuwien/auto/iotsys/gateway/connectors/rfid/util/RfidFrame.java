package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util;

import java.util.logging.Logger;

public class RfidFrame {

	private static final Logger log = Logger.getLogger(RfidFrame.class.getName());
	public enum STATES_GET_PACKET {
		STATE_STX(0x01), STATE_STATION_ID(0x02), 
		STATE_LEN(0x03), STATE_DATA(0x04), STATE_BCC(0x05), STATE_ETX(0x06), 
		STATE_END(0x7);

		private int state;

		private STATES_GET_PACKET(int state) {
			this.state = state;
		}

		public int getState() {
			return this.state;
		}
	}
	public static RfidFrame instance = null;

	private byte[] packetData;
	private int packetLength;
	private RFIDPacketHeader header;
	private RFIDPacket packet;
	private RFIDData data;
	private StringBuffer stringData;
	

	public RfidFrame(byte[] packetData, int packetLength)
	{
		instance = this;
		this.header = new RFIDPacketHeader();
		this.packetData = packetData;
		this.packetLength = packetLength;
	}

	public byte[] getPacketData() {
		return packetData;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public String dataToString()
	{
		//return packetData.toString();
		return stringData.toString();
	}
	
	private void setStringData(StringBuffer strBuff)
	{
		
		stringData = strBuff;
	}
	
	public void readPacket()
	{
		log.info("read packet!");
		STATES_GET_PACKET state = STATES_GET_PACKET.STATE_STX;

		log.info("size: " + packetLength);
		if(packetLength <= 0) 
			return;

		byte station_id = 0;
		byte data_len = 0;
		byte bcc_calc = 0;
		byte t_data[] = new byte[256];
		byte data_pos = 0;

		
			
		//log.info("before loop!");
		
		for (int i = 0; i < packetLength; i++) {
			byte tempData = packetData[i];
			//log.info("for loop!");
			
			switch (state) {
			case STATE_STX: 
				if(tempData == 0x02)
				{
					bcc_calc = 0;
					data_pos = 0;
					data_len = 0;
					station_id = 0;

					//log.info("STX!");
					state = STATES_GET_PACKET.STATE_STATION_ID;
				}
				else {
					log.info("STX byte not found!");
					state = STATES_GET_PACKET.STATE_END;
				}
				break;
			case STATE_STATION_ID:
				//log.info("Station ID!");
				station_id = tempData;
				header.setStationID(station_id);			
				state = STATES_GET_PACKET.STATE_LEN;
				break;
			case STATE_LEN:
				//log.finest("State len!");
				
				data_len = tempData;
				header.setDataLength(data_len);
				state = STATES_GET_PACKET.STATE_DATA;
				break;
			case STATE_DATA:
				//log.info("state data!");

				t_data[data_pos++] = tempData;
				bcc_calc ^= tempData;
				/*
				if(data_pos < data_len)
				{
					data[data_pos++] = tempData;
					bcc_calc ^= tempData;
				}*/
				if (data_pos == header.getDataLength()) 
				{
					state = STATES_GET_PACKET.STATE_BCC;
					data = new RFIDData(t_data); 
					packet = new RFIDPacket(header, data);
				}
				break;
			case STATE_BCC:
				// log.info("State bcc!");

				bcc_calc ^= header.getStationID() ^ header.getDataLength();
				
				if(bcc_calc != tempData)
				{
/*					log.info("wrong bcc!\n");
					log.info("bcc recv: " + Integer.toHexString(tempData));
					log.info("calc_bcc: " + Integer.toHexString(bcc_calc));*/
					state = STATES_GET_PACKET.STATE_END;
				} else {
					state = STATES_GET_PACKET.STATE_ETX;
				}
				break;
			case STATE_ETX:
				// log.info("state ETX!");

				if(tempData == 0x03)
				{
					log.info("");
					log.info("station_id:" + Integer.toHexString(header.getStationID()));
					log.info("data_len:" + Integer.toHexString(header.getDataLength()));
					
					log.info("Received Extracted Data: ");
					StringBuffer hexString = new StringBuffer();
					for (int j = 0; j < header.getDataLength(); j++) {
						String hex = Integer.toHexString(0xFF & t_data[j]);
						if (hex.length() == 1)
							hexString.append('0');

						hexString.append(hex + " ");
					}
					log.info(hexString.toString());
					setStringData(hexString);
					log.info("dataToString: " + dataToString());
					
					log.info("bcc:" + Integer.toHexString(bcc_calc));
					log.info("");
					
					state = STATES_GET_PACKET.STATE_STX;
					
				} else {
					log.info("no etx detected!\n");
					state = STATES_GET_PACKET.STATE_END;
				}
				break;
			case STATE_END:
				// log.info("STATE_END\n");
				break;
			default:
				state = STATES_GET_PACKET.STATE_STX;
				log.info("default state\n");
				break;
			}
		}
	}
/*
	public RFIDPacket getPacket() {
		return packet;
	}

	public RFIDPacketHeader getPacketHeader() {
		return header;
	}
*/
}
