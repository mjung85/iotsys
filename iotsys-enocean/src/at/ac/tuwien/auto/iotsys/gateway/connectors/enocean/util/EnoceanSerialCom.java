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

package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public final class EnoceanSerialCom implements Runnable,
		SerialPortEventListener {
	private static final Logger log = Logger.getLogger(EnoceanSerialCom.class
			.getName());

	private static final int SERIAL_TIMEOUT = 2000;
	private static final int BAUD_RATE = 57600;

	private SerialPort serialPort;
	private Thread readThread;
	private InputStream inputStream;
	private CommPort commPort;
	ESP3Frame frame;

	public EnoceanSerialCom(String portName) {
		log.info("EnoceanSerialCom loaded ...");

		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
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

			readThread = new Thread(this);
			readThread.start();
		}
	}

	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
				Thread.sleep(250);
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
				// System.out.println(hexString.toString());

				frame = new ESP3Frame(buffer, numBytes);
				frame.readPacket();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closePort() {
		serialPort.close();
	}
}
