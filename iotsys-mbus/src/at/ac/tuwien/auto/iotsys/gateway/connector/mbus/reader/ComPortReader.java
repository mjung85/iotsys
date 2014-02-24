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

package at.ac.tuwien.auto.iotsys.gateway.connector.mbus.reader;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.TelegramManagerInterface;
import gnu.io.*;

public class ComPortReader implements Runnable, SerialPortEventListener {
    private InputStream inputStream;
    private SerialPort serialPort;
    private Thread readThread;
    private TelegramManagerInterface tManager;
    
    private static Logger log = Logger.getLogger(ComPortReader.class.getName());
    
    //public static String COMPORT = "/dev/ttyAMA0";
    private static String COMPORT = "COM4";

    public ComPortReader(CommPortIdentifier portId, TelegramManagerInterface tManager) {
    	
    	this.tManager = tManager;
        try {
            serialPort = (SerialPort) portId.open("SimpleSmartMeterReader", 2000);
        } catch (PortInUseException e) {
        	e.printStackTrace();
        }
        try {
            inputStream = serialPort.getInputStream();
        } catch (IOException e) {
        	e.printStackTrace();
        }
	try {
            serialPort.addEventListener(this);
	} catch (TooManyListenersException e) {
		e.printStackTrace();
	}
        serialPort.notifyOnDataAvailable(true);
        try {
            serialPort.setSerialPortParams(2400,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        } catch (UnsupportedCommOperationException e) {
        	e.printStackTrace();
        }
        readThread = new Thread(this);
        readThread.start();
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
    }

    public void serialEvent(SerialPortEvent event) {
        switch(event.getEventType()) {
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
            byte[] readBuffer = new byte[256];
            
            //after Data is available, give it a couple of seconds to process
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            	e.printStackTrace();            	
            }

            try {
            	int numBytes = 0;
                while (inputStream.available() > 0) {
                    numBytes += inputStream.read(readBuffer);
                }
               
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < readBuffer.length && i < numBytes; i++) {
	                String hex = Integer.toHexString(0xFF & readBuffer[i]);
	                if (hex.length() == 1) {
	                    // could use a for loop, but we're only dealing with a single byte
	                    hexString.append('0');
	                }
	                hexString.append(hex + " ");
                }               
                tManager.addTelegram(hexString.toString());
                
            } catch (IOException e) {
            	e.printStackTrace();
            }
            break;
        }
    }
    
    public static CommPortIdentifier lookupPorts(String comPort) {
    	Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
    	
        while (portList.hasMoreElements()) {
        	CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            
                 if (portId.getName().equals(comPort)) {
                	 log.info("Found com port: " + comPort);
                	 return portId;
                }
            }
        }
        return null;
    }
   
    public void closePort(){
    	serialPort.close();
    }
    
}
