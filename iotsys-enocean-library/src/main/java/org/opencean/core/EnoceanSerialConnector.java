package org.opencean.core;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.utils.CircularByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EnOcean connector for serial port communication.
 * 
 * @author Evert van Es
 * @since 1.3.0
 */
public class EnoceanSerialConnector implements ProtocolConnector {

    private static final Logger logger = LoggerFactory.getLogger(EnoceanSerialConnector.class);

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
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(device);

            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

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

    @Override
    public void disconnect() {
        logger.debug("Interrupt serial connection");
        byteStreamPipe.stop();

        logger.debug("Close serial stream");
        try {
            out.close();
            serialPort.close();
            buffer.stop();
        } catch (IOException e) {
            logger.warn("Could not fully shut down EnOcean driver", e);
        }

        logger.debug("Ready");
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
