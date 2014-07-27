package org.opencean.core;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.utils.CircularByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * EnOcean connector for serial port communication.
 * 
 * @author Evert van Es
 * @since 1.3.0
 */
public class EnoceanSerialConnector implements ProtocolConnector {

    private static final Logger logger = Logger.getLogger(EnoceanSerialConnector.class.getName());

    InputStream in = null;
    DataOutputStream out = null;
    SerialPort serialPort = null;
    EnoceanByteStreamPipe byteStreamPipe = null;

    private CircularByteBuffer buffer;

    public EnoceanSerialConnector() {
    }

    @Override
    public void connect(String device) {
        try {
        	CommPortIdentifier portIdentifier = this.lookupPorts(device);
            //CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(device);

        	SerialPort commPort = (SerialPort) portIdentifier.open(this.getClass().getName(), 2000);

            serialPort = (SerialPort) commPort;
            serialPort.setSerialPortParams(57600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            in = serialPort.getInputStream();
            out = new DataOutputStream(serialPort.getOutputStream());

            out.flush();

            buffer = new CircularByteBuffer(Byte.MAX_VALUE * Byte.MAX_VALUE + 2 * Byte.MAX_VALUE);
            byteStreamPipe = new EnoceanByteStreamPipe(in, buffer);
            new Thread(byteStreamPipe).start();

            // Runtime.getRuntime().addShutdownHook(new Thread() {
            //
            // @Override
            // public void run() {
            // disconnect();
            // }
            // });

        } catch (Exception e) {
            throw new RuntimeException("Could not init comm port", e);
        }
    }
    
    private CommPortIdentifier lookupPorts(String comPort) {
    	Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
    	
        while (portList.hasMoreElements()) {
        	CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();        	    	
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {           
                 if (portId.getName().equals(comPort)) {
                	 logger.info("Found com port: " + comPort);
                	 return portId;
                }
            }
        }
        return null;
    }

    @Override
    public void disconnect() {
        logger.info("Interrupt serial connection");
        byteStreamPipe.stop();

        logger.info("Close serial stream");
        try {
            out.close();
            serialPort.close();
            buffer.stop();
        } catch (IOException e) {
        	logger.log(Level.WARNING, "Could not fully shut down EnOcean driver", e);            
        }

        logger.info("Ready");
    }

    @Override
    public byte get() {
        return buffer.get();
    }

    @Override
    public short getShort() {
        return buffer.getShort();
    }

    @Override
    public void get(byte[] data) {
        buffer.get(data);
    }

    @Override
    public void mark() {
        buffer.mark();
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    @Override
    public void write(byte[] data) {
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Could not write", e);
        }
    }

}
