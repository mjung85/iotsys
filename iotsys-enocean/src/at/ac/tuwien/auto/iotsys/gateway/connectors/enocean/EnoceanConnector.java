package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util.ESP3Frame;

public class EnoceanConnector implements Connector, SerialPortEventListener {
	private static final Logger log = Logger.getLogger(EnoceanConnector.class.getName());

	private String port;

	private static final int SERIAL_TIMEOUT = 2000;
	private static final int BAUD_RATE = 57600;

	private SerialPort serialPort;
	private InputStream inputStream;
	private CommPort commPort;
	ESP3Frame frame;

	public EnoceanConnector(String portName) {
		this.port = portName;
	}
	
	private final Hashtable<String, ArrayList<EnoceanWatchdog>> watchDogs = new Hashtable<String, ArrayList<EnoceanWatchdog>>();
		
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
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

			log.info("Start listening on USB port ...");

			//readThread = new Thread();
			//readThread.start();
		}

	}

	@Override
	public void disconnect() throws Exception {
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
					StringBuffer hexString = new StringBuffer();
					for (int i = 0; i < buffer.length && i < numBytes; i++) {
						String hex = Integer.toHexString(0xFF & buffer[i]);
						if (hex.length() == 1)
							hexString.append('0');
	
						hexString.append(hex + " ");
					}
					//System.out.println(hexString.toString());
		
					synchronized (watchDogs) {
						frame = new ESP3Frame(buffer, numBytes);
						frame.readPacket();
						String address = frame.getPacket().telegram.getSenderID().toString();
						log.finest("notify watchdog: " + address);
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

	
	public void addWatchDog(String observation, EnoceanWatchdog enoceanWatchdog) {
		synchronized (watchDogs) {
			if (!watchDogs.containsKey(observation)) {
				watchDogs.put(observation,
						new ArrayList<EnoceanWatchdog>());
			}
			log.finest("Adding watchdog for address "
					+ observation);
			watchDogs.get(observation).add(enoceanWatchdog);
		}
	}
}