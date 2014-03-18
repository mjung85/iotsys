package at.ac.tuwien.auto.iotsys.gateway.connectors.rfid;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.rfid.util.RfidFrame;

public class RfidConnector implements Connector, SerialPortEventListener 
{

	private static final Logger log = Logger.getLogger(RfidConnector.class.getName());

	private String port;

	private static final int SERIAL_TIMEOUT = 2000;
	//private static final int BAUD_RATE = 9600;
	
	private static final int BAUD_RATE = 460800;

	private SerialPort serialPort;
	private InputStream inputStream;
	private static OutputStream outputStream;
	private CommPort commPort;
	public RfidFrame frame;
	
	private byte STX = 0;
	private byte SID = 1;
	private byte LEN = 2;
	private byte DATA = 3;
	private byte DATAM = 4;	
	
	public RfidConnector(String portName) {
		this.port = portName;
	}
	private final ArrayList<RfidWatchdog> watchDogs = new ArrayList<RfidWatchdog>();
	
	public static void rfidSend (byte data[])
	{
		//log.info("sending data" + data.toString());

		
	}
	
	public void sendCommand(String cmd)
	{
		byte[] data = new byte[7];
		byte bcc = 0;
		byte dataSize = 6;
		
		data[STX] = (byte)0x02;
		data[SID] = (byte)0x01;
		bcc = (byte) (bcc^0x01);

		if(cmd == "m")
		{
			data[LEN] = (byte)0x02;
			bcc = (byte) (bcc^0x02);
			
			data[DATA] = (byte)cmd.toCharArray()[0];
			bcc = (byte) (bcc^(byte)cmd.toCharArray()[0]);
			
			data[DATAM] = 0x0d;
			bcc = (byte) (bcc^0x0d);

			data[DATAM+1] = (byte)bcc;
			data[DATAM+2] = (byte)0x03;
			
			dataSize = 7;
			
		} else {
			
			data[LEN] = (byte)0x01;
			bcc = (byte) (bcc^0x01);
			
			data[DATA] = (byte)cmd.toCharArray()[0];
			bcc = (byte) (bcc^(byte)cmd.toCharArray()[0]);

			data[DATA+1] = (byte)bcc;
			data[DATA+2] = (byte)0x03;
		}

		log.info("cmd: " +  (byte)cmd.toCharArray()[0]);
		StringBuffer hexString = new StringBuffer();
		for (int j = 0; j < data.length && j < 7; j++) {
			String hex = Integer.toHexString(0xFF & data[j]);
			if (hex.length() == 1)
				hexString.append('0');

			hexString.append(hex + " ");
		}
		
		log.info(hexString.toString());
		try {
			
			outputStream.write(data, 0, dataSize);
			//outputStream.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info("sending data" + data + " size: " + dataSize);
		
		//rfidSend(data);

	}
	
	@Override
	public void connect() throws Exception
	{
		log.info("RfidSerialCom loaded " + port + ":" + BAUD_RATE);

		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(port);
			commPort = portIdentifier.open(portIdentifier.getName(),
					SERIAL_TIMEOUT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (commPort instanceof SerialPort) {
			try {
				serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(BAUD_RATE,
						SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				inputStream = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			log.info("Start listening on USB ("+port+" ) port ...");
		}	
		sendCommand("s");
	}
	
	@Override
	public void disconnect() throws Exception {
		log.info("RFID serial disconnect");
		serialPort.removeEventListener();
		serialPort.close();
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {
		log.info("RFID serial event");
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] buffer = new byte[256];
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				int numBytes = 0;

				while (inputStream.available() > 0) {
					numBytes += inputStream.read(buffer);
				}
				log.info("Received Raw Frame: ");
				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < buffer.length && i < numBytes; i++) {
					String hex = Integer.toHexString(0xFF & buffer[i]);
					if (hex.length() == 1)
						hexString.append('0');

					hexString.append(hex + " ");
				}
				log.info(hexString.toString());

				frame = new RfidFrame(buffer, numBytes);
				log.info("Created RfidFrame Object");
				
				log.info("Calling readPacket()");
				log.info("Frame PacketLength: " + frame.getPacketLength());
				frame.readPacket();
				notifyWatchDogs(frame.dataToString());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//sendCommand();
	}

	public void addWatchDog(RfidWatchdog rfidWatchdog) {		
		synchronized (watchDogs) {			
			watchDogs.add(rfidWatchdog);
		}
	}
	
	public void notifyWatchDogs(String tag){
		synchronized (watchDogs){
			for(RfidWatchdog watchDog : watchDogs){
				watchDog.notifyWatchDog(tag);
			}
		}
	}
}
