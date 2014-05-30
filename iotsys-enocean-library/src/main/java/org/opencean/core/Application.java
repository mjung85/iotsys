package org.opencean.core;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.QueryIdCommand;
import org.opencean.core.packets.RadioPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stand-alone Application for reading EnOcean packages
 * 
 * @author Thomas Letsch (contact@thomas-letsch.de)
 * 
 */
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        logger.info("starting..");
        String port = args[0];
        ProtocolConnector connector = new EnoceanSerialConnector();
        connector.connect(port);
        ESP3Host esp3Host = new ESP3Host(connector);
        
        //BasicPacket packet = new QueryIdCommand();        
        //EventPacket packet = new 
//        Header h;
//        Payload p = new Payload();
//        RawPacket raw;
//        EventPacket event;
        RadioPacket radio;
        
        byte dataOn[] = {(byte) 0xF6, (byte) 0x50, (byte) 0x00, (byte) 0x25, (byte) 0xA2, (byte) 0xDC, (byte) 0x30};
        byte optDataOn[] = {(byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x32, (byte) 0x00};
        byte dataOff[] = {(byte) 0xF6, (byte) 0x70, (byte) 0x00, (byte) 0x25, (byte) 0xA2, (byte) 0xDC, (byte) 0x30};
        byte optDataOff[] = {(byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x3A, (byte) 0x00};   
        
//        h = new Header((byte)0x05, (short)0x01, (byte)0x00);        
            
//        esp3Host.start();
//        for(byte i=0x30;i<=0x60;i++){
        //	optDataOn[5]=i;
//        p.setData(dataOn);
//        p.setOptionalData(optDataOn);
//        p.initCRC8();
//        BasicPacket packet = new RadioPacket((new RawPacket(new Header((byte)0x01, (short)0x07, (byte)0x07), p)));
        
//        h = new Header((byte)0x01, (short)0x0007, (byte)0x07);        
//        
//        p.setData(dataOn);
//        p.setOptionalData(optDataOn);      
//        p.initCRC8();       
//        logger.info("Payload: " + new ByteArray(p.toBytes()) +" Payload CRC Valid: " + p.isValid());
//        raw = new RawPacket(h ,p);
//        logger.info("Raw packet: " +raw);
        radio = new RadioPacket(dataOn, (byte) 0x01, 0xFFFFFFFF, (byte) 0x4D, (byte) 0x00);
        
        BasicPacket packet = radio;
        logger.info("Send packet: " + packet.toString());
        esp3Host.sendRadio(packet);
        Thread.sleep(200);
        esp3Host.start();        
//        }
//        esp3Host.start();  
    }
}
