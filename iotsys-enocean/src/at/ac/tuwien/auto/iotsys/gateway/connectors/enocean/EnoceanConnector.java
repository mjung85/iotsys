/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

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