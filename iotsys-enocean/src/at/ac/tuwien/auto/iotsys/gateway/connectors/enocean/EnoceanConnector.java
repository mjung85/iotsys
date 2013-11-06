package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.CRC8Hash;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.DeviceID;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Frame;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3PacketHeader.PacketType;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Response;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Telegram;

public class EnoceanConnector implements Connector, SerialPortEventListener {
	private static final Logger log = Logger.getLogger(EnoceanConnector.class.getName());

	private String port;

	private static final int SERIAL_TIMEOUT = 2000;
	private static final int BAUD_RATE = 57600;

	private SerialPort serialPort;
	private InputStream inputStream;
	private static OutputStream outputStream;
	private CommPort commPort;
	ESP3Frame frame;

	public EnoceanConnector(String portName) {
		this.port = portName;
	}

	private final Hashtable<String, ArrayList<EnoceanWatchdog>> watchDogs = new Hashtable<String, ArrayList<EnoceanWatchdog>>();
	//private SynchronousQueue<ESP3Telegram> responseQueue = new SynchronousQueue<ESP3Telegram>();
	private LinkedBlockingQueue<ESP3Telegram> responseQueue = new LinkedBlockingQueue<ESP3Telegram>();
	protected DeviceID BaseID;

	@Override
	public void connect() throws Exception {
		log.info(port + ", " + BAUD_RATE);

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

			log.info("Start listening on USB port ...");
		}

		addWatchDog("RESPONSE", new EnoceanWatchdog() {
			@Override
			public void notifyWatchDog(ESP3Frame payload) {
				if (payload.getPacket().getPacketType() == PacketType.RESPONSE)
					log.info("RESPONSE Watchdog: " + payload.getPacket().telegram.getPayloadAsString());
				responseQueue.offer(payload.getPacket().telegram);
				log.info("RESPONSE Telegram added to responseQueue");
			}
		});

		// read base id
		// command 8 
		byte[] readBaseID = new byte[8];
		int i = 0;
		readBaseID[i++] = (byte)0x55;
		readBaseID[i++] = (byte)0x00;
		readBaseID[i++] = (byte)0x01;
		readBaseID[i++] = (byte)0x00;
		readBaseID[i++] = (byte)0x05;
		CRC8Hash.calculate(readBaseID, 1, 4);
		readBaseID[i++] = (byte)(CRC8Hash.calculate(readBaseID, 1, 4));
		readBaseID[i++] = (byte)0x08;
		byte[] crc8d = new byte[1];
		crc8d[0] = (byte)0x08;
		readBaseID[i++] = (byte)(CRC8Hash.calculate(crc8d));

		serialSend(readBaseID);
		ESP3Telegram response = responseQueue.poll(500, TimeUnit.MILLISECONDS);
		if(response == null) 
		 {
			 log.info("RESPONSE is NULL!");
		 } else {
			 log.info("Received reponse from responseQueue: " + response.getPayloadAsString());
			 BaseID = DeviceID.fromByteArray(response.getPayload(), 1);
			 log.info("BaseID: " + BaseID.toString());
		 }
		log.info("Received reponse from responseQueue: " + response.getPayloadAsString());
		//ESP3Telegram readBaseIDTelegram = new ESP3TelegramCommonCommand(readBaseID, 1, 0);
		BaseID = DeviceID.fromByteArray(response.getPayload(), 1);
		 
		

	}
	public DeviceID getBaseID()
	 {
	return BaseID;
	}
	@Override
	public void disconnect() throws Exception {
		serialPort.removeEventListener();
		serialPort.close();
	}

	public void serialEvent(SerialPortEvent event) {
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
			byte[] buffer = new byte[4096];
			try {
				Thread.sleep(125);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			try {
				int numBytes = 0;

				while (inputStream.available() > 0) {
					numBytes += inputStream.read(buffer);
				}

				System.out.print("EnoceanConnector received raw packet: ");
				StringBuffer hexString = new StringBuffer();
				for (int i = 0; i < buffer.length && i < numBytes; i++) {
					String hex = Integer.toHexString(0xFF & buffer[i]);
					if (hex.length() == 1)
						hexString.append('0');

					hexString.append(hex + " ");
				}
				System.out.println(hexString.toString());

				synchronized (watchDogs) {
					frame = new ESP3Frame(buffer, numBytes);
					frame.readPacket();
					String address = new String();
					if (frame.getPacket().telegram.getSenderID() != null)
						address = frame.getPacket().telegram.getSenderID().toString();

					if(frame.getPacketHeader().getPacketType() == ESP3PacketHeader.PacketType.RESPONSE)
						address = "RESPONSE";

					// frame.getPacket().telegram.getPayloadAsString();
					log.info("notify watchdog: " + address);
					if (watchDogs.containsKey(address)) {
						for (EnoceanWatchdog w: watchDogs.get(address)) {
							w.notifyWatchDog(frame);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void serialSend (byte data[])
	{
		try {
			outputStream.write(data);
			System.out.print("EnoceanConnector sent raw packet: ");
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < data.length; i++) {
				String hex = Integer.toHexString(0xFF & data[i]);
				if (hex.length() == 1)
					hexString.append('0');

				hexString.append(hex + " ");
			}
			System.out.println(hexString.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ESP3Response send (byte[] data) 
	{
		serialSend(data);
		ESP3Response response = (ESP3Response)getResponse();
		
		return response;
	}
	
	private ESP3Telegram getResponse()
	{
		ESP3Telegram response = null;
		
		try {
			response = responseQueue.poll(500, TimeUnit.MILLISECONDS);
			// response = responseQueue.poll();
			// responseQueue.clear();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// response = responseQueue.poll();
		return response;
	}
	
	public void addWatchDog(String observation, EnoceanWatchdog enoceanWatchdog) {
		synchronized (watchDogs) {
			if (!watchDogs.containsKey(observation)) {
				watchDogs.put(observation,
						new ArrayList<EnoceanWatchdog>());
			}
			log.info("Adding watchdog for address "
					+ observation);
			watchDogs.get(observation).add(enoceanWatchdog);
		}
	}
}