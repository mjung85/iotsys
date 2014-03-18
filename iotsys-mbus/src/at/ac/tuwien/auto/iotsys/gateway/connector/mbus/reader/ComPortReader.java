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
import at.ac.tuwien.auto.iotsys.gateway.connector.mbus.util.Converter;
import gnu.io.*;

public class ComPortReader implements Runnable, SerialPortEventListener {
    private InputStream inputStream;
    private OutputStream outputStream;
    private SerialPort serialPort;
    private Thread readThread;
    private TelegramManagerInterface tManager;
    private boolean serialPortIsOpen;
    private int interval = 60; // interval in seconds
    private int intervalCnt;
    private byte address = 1;
    
//    private static String messageString_SND_NKE = "10 40 01 41 16 \r\n";
//	private static String messageString_REQ_UD2 = "10 7B 01 7C 16 \r\n";
//	static byte[] byteArray_SND_NKE = {0x10,0x40,0x01,0x41,0x16};
//	static byte[] byteArray_REQ_UD2 = {0x10,0x7B,0x01,0x7C,0x16};
    
    private static Logger log = Logger.getLogger(ComPortReader.class.getName());
    
    //public static String COMPORT = "/dev/ttyAMA0";
    private static String COMPORT = "COM4";
    
    public ComPortReader(CommPortIdentifier portId, TelegramManagerInterface tManager, int interval, byte address){
    	this(portId, tManager);
    	this.interval = interval;
    	this.address = address;    	
    }

    public ComPortReader(CommPortIdentifier portId, TelegramManagerInterface tManager) {
    	
    	this.serialPortIsOpen = false;
    	this.intervalCnt = 0;    	
    	
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
			outputStream = serialPort.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	try {
    		serialPort.addEventListener(this);
    	} catch (TooManyListenersException e) {
    		e.printStackTrace();
    	}
    	try {
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}	
    	try {
    		serialPort.setSerialPortParams(2400,
    				SerialPort.DATABITS_8,
    				SerialPort.STOPBITS_1,
    				SerialPort.PARITY_EVEN);
    	} catch (UnsupportedCommOperationException e) {
    		e.printStackTrace();
    	}
    	try {
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException e) {						
			e.printStackTrace();
		}
		serialPort.setRTS(false); 
		serialPort.setDTR(false);	
		
    	serialPortIsOpen=true;
    	readThread = new Thread(this);
    	readThread.start();
    }

    public void run() {
    	while(serialPortIsOpen){
    		try {
                Thread.sleep(1000);
                intervalCnt++;
//                System.out.println("DEBUG: PortStatus: " +serialPortIsOpen +" IntervalCount " +intervalCnt);
                if(intervalCnt == interval){
                	startReadingMeter();
                	intervalCnt = 0;
                }
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
    	}        
    }      
    
    byte[] create_SND_NKE(byte address){
    	byte[] tempBA = new byte[5];
    	tempBA[0] = 0x10;	// Start 10h
    	tempBA[1] = 0x40;	// C-Field: 0x40 for SND_NKE 
    	tempBA[2] = address;// A-Field
    	tempBA[3] = (byte)(tempBA[1]+tempBA[2]);	// Checksum
    	tempBA[4] = 0x16;	// Stop 16h
    	    	
    	System.out.println("DEBUG create_SND_NKE: " +Converter.convertByteArrayToString(tempBA));
    	return tempBA;
    }
    
    byte[] create_REQ_UD2(byte address){
    	byte[] tempBA = new byte[5];
    	tempBA[0] = 0x10;	// Start 10h
    	tempBA[1] = 0x7B;	// C-Field: 0x7B for REQ_UD2 
    	tempBA[2] = address;// A-Field
    	tempBA[3] = (byte)(tempBA[1]+tempBA[2]);	// Checksum
    	tempBA[4] = 0x16;	// Stop 16h
    	
    	System.out.println("DEBUG create_REQ_UD2: " +Converter.convertByteArrayToString(tempBA));
    	return tempBA;
    }
    
    void sendSerialPortMessage(byte[] message)
	{		
		if (serialPortIsOpen != true)
			return;
		try {
			outputStream.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 void startReadingMeter()
	 {
//		 sendSerialPortMessage(create_SND_NKE(address));
		 sendSerialPortMessage(create_REQ_UD2(address));
	 }
    
    void serialPortDataAvailable() {	
    	byte[] readBuffer = new byte[512];
    	
		try {			
			int numBytes=0;
			while(inputStream.available() > 0) {
				numBytes += inputStream.read(readBuffer);		
			}			
			if(Integer.toHexString(readBuffer[0] & 0xFF).equalsIgnoreCase("e5")) {
//				System.out.println("DEBUG: Received single character E5");
				sendSerialPortMessage(create_REQ_UD2(address));
			}
			if(readBuffer[0] == 0x68) {
				System.out.println("DEBUG: Revceived data");				
				// add telegram
				tManager.addTelegram(Converter.convertByteArrayToString(readBuffer));
			}			
		} catch (IOException e) {
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
            //after Data is available, give it a couple of seconds to process
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            	e.printStackTrace();            	
            }
            serialPortDataAvailable();            
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
    	try {
			if (inputStream != null) inputStream.close();
			if (outputStream != null) outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		serialPort.close();	
		serialPortIsOpen=false;
    }
    
    public void setAdress(byte address){
    	this.address = address;
    }
    
    public byte getAddress(){
    	return this.address;
    }
    
    public void setInterval(int interval){
    	this.interval = interval;
    }
    
    public int getInterval(){
    	return this.interval;
    }
}
