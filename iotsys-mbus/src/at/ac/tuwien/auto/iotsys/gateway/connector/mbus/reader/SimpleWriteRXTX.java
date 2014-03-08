package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.reader;

/*
 * @(#)SimpleWrite.java	1.12 98/06/25 SMI
 * 
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license
 * to use, modify and redistribute this software in source and binary
 * code form, provided that i) this copyright notice and license appear
 * on all copies of the software; and ii) Licensee does not utilize the
 * software in a manner which is disparaging to Sun.
 * 
 * This software is provided "AS IS," without a warranty of any kind.
 * ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS
 * BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
 * HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING
 * OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * This software is not designed or intended for use in on-line control
 * of aircraft, air traffic, aircraft navigation or aircraft
 * communications; or in the design, construction, operation or
 * maintenance of any nuclear facility. Licensee represents and
 * warrants that it will not use or redistribute the Software for such
 * purposes.
 */
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.TooManyListenersException;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.telegrams.Telegram;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;
//import javax.comm.*;
import gnu.io.*;

/**
 * Class declaration
 * 
 * 
 * @author
 * @version 1.00, 03/08/14
 */
public class SimpleWriteRXTX {
	static Enumeration<?> portList;
	static CommPortIdentifier portId;
	//static String messageString = "Hello, world!";
	static SerialPort serialPort;
	static OutputStream outputStream;
	static InputStream inputStream;
	static boolean serialPortIsOpen = false;
	static boolean meterFound = false;
	static boolean dataFound = false;
	static boolean outputBufferEmptyFlag = false;
	
	static String messageString_SND_NKE = "10 40 01 41 16";
	static String messageString_REQ_UD2 = "10 7B 01 7C 16";
	static byte[] byteArray_SND_NKE = {0x10,0x40,0x01,0x41,0x16};
	static byte[] byteArray_REQ_UD2 = {0x10,0x7B,0x01,0x7C,0x16};
	
	static Telegram telegram;

	private static final Logger log = Logger.getLogger(SimpleWriteRXTX.class
			.getName());	

	/**
	 * Method declaration
	 * 
	 * 
	 * @param args
	 * 
	 * @see
	 */
	public static void main(String[] args) {		
		boolean portFound = false;
		String defaultPort = "COM4";

		if (args.length > 0) {
			defaultPort = args[0];
		}

		portList = CommPortIdentifier.getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = (CommPortIdentifier) portList.nextElement();

			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {

				if (portId.getName().equals(defaultPort)) {
					log.finest("Found port " + defaultPort);

					portFound = true;

					try {
						serialPort = (SerialPort) portId.open("SimpleWrite",
								2000);
					} catch (PortInUseException e) {
						log.severe("Port in use. " + portId);

						continue;
					}

					try {
						outputStream = serialPort.getOutputStream();
					} catch (IOException e) {
					}
				
					try {
						inputStream = serialPort.getInputStream();
					} catch (IOException e) {
						System.out.println("Keinen Zugriff auf InputStream");
					}
					try {						
						serialPort.addEventListener(new SerialPortReader());
					} catch (TooManyListenersException e) {
						System.out.println("TooManyListenersException für Serialport");
					}									
					
					try {
						serialPort.notifyOnDataAvailable(true);
					} catch (Exception e) {
						log.severe("Error setting event notification"
								+ e.toString());
					}					
					serialPortIsOpen=true;

					try {
						serialPort.setSerialPortParams(2400,
								SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
								SerialPort.PARITY_EVEN);
					} catch (UnsupportedCommOperationException e) {
					}
					
					try {
						serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					} catch (UnsupportedCommOperationException e) {						
						e.printStackTrace();
					}
					serialPort.setRTS(false); 
					serialPort.setDTR(false);	

//					try {
//						serialPort.notifyOnOutputEmpty(true);
//					} catch (Exception e) {
//						log.severe("Error setting event notification"
//								+ e.toString());
//					}

//					log.finest("Writing \"" + messageString_SND_NKE + "\" to "
//							+ serialPort.getName());					

//					try {
//						outputStream.write(messageString.getBytes());					
//					} catch (IOException e) {
//						System.out.println("Error while writing: " +e);
//					}

//					try {
//						Thread.sleep(5000); // Be sure data is xferred before
//											// closing
//					} catch (Exception e) {
//					}					
					
					startReadingMeter();		
					
					try {
						System.out.println("Waiting for processing data");	
						Thread.sleep(15000); 												
					} catch (Exception e) {
						System.out.println("Error while sleeping: " +e);
					}
										
					closePort();
				}
			}
		}
		
		System.out.println("Finished successfully \nMeter Found: " + meterFound + "\ndataFound: " +dataFound );

		if (!portFound) {
			log.warning("port " + defaultPort + " not found.");
		}
	}
	
	static void sendSerialPortMessage(byte[] message)
	{		
		System.out.println("Sende: " + message.toString());
		if (serialPortIsOpen != true)
			return;
		try {
			outputStream.write(message);
		} catch (IOException e) {
			System.out.println("Fehler beim Senden");
		}
	}
	
	 static void startReadingMeter()
	 {
		 sendSerialPortMessage(byteArray_SND_NKE);
	 }
	 
//	 public static String ConvertByteArrayToString(byte[] ba){		 
//		 StringBuilder sb = new StringBuilder();
//		 for (byte b : ba) {
//			 sb.append(String.format("%02X ", b));
//		 }		 
//		 return sb.toString();
//	 }
	
	static void serialPortDataAvailable() {
		telegram = new Telegram();	
		byte[] readBuffer = new byte[512];

		try {			
			int num=0;
			while(inputStream.available() > 0) {
				num = inputStream.read(readBuffer, 0, readBuffer.length);		
			}
			System.out.println("Empfange: "+ Integer.toHexString(readBuffer[0] & 0xFF));
			if(Integer.toHexString(readBuffer[0] & 0xFF).equalsIgnoreCase("e5")) {
				System.out.println("Einzelzeichen E5 empfangen");
				sendSerialPortMessage(byteArray_REQ_UD2);
				meterFound=true;
			}
			if(readBuffer[0] == 0x68) {
				System.out.println("Daten empfangen");				
				// add telegram
				telegram.createTelegram(Converter.convertByteArrayToString(readBuffer));		
				telegram.parse();
				telegram.debugOutput();
				dataFound=true;

			}
		} catch (IOException e) {
			System.out.println("Fehler beim Lesen empfangener Daten");
		}
	}
	
	 static public void closePort(){    	
	    	try {
				if (inputStream != null) inputStream.close();
				if (outputStream != null) outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			serialPort.close();	
			serialPortIsOpen=false;
	    }

	static class SerialPortReader implements SerialPortEventListener {
		public void serialEvent(SerialPortEvent event) {
			System.out.println("serialPortEventlistener");
			switch (event.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				System.out.println("wait 2 seconds for data");
				 //after Data is available, give it a couple of seconds to process
	            try {
	                Thread.sleep(2000);
	            } catch (InterruptedException e) {
	            	e.printStackTrace();            	
	            }	            
				serialPortDataAvailable();
				break;
			case SerialPortEvent.BI:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.FE:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				System.out.println("OutputBufferEmpty");
				break;
			case SerialPortEvent.PE:
			case SerialPortEvent.RI:
			default:
			}
		}
	}
}
