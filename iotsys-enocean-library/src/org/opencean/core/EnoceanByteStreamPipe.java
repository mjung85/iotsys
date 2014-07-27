package org.opencean.core;

import java.io.IOException;
import java.io.InputStream;

import org.opencean.core.utils.CircularByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnoceanByteStreamPipe implements Runnable {

    private static final Logger logger = Logger.getLogger(EnoceanByteStreamPipe.class.getName());

    private boolean running = true;
    private InputStream in = null;
    private CircularByteBuffer buffer;

    public EnoceanByteStreamPipe(InputStream in, CircularByteBuffer buffer) {
        this.in = in;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (running) {
            try {
                byte readByte = (byte) in.read();
                logger.finest(String.format("Received " + readByte));
                buffer.put(readByte);
            } catch (Exception e) {
            	logger.log(Level.SEVERE, "Error while reading from COM port. Stopping.", e);               
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        running = false;
        try {
            in.close();
        } catch (IOException e) {
        	logger.log(Level.SEVERE, "Error while closing COM port.", e);            
        }
    }

}
