package org.opencean.core;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.QueryIdCommand;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RadioPacket4BS;
import org.opencean.core.utils.ByteArray;
import org.opencean.core.utils.RLC;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stand-alone Application for reading EnOcean packages
 * 
 * @author Jürgen Schober
 * 
 */
public class Application {

    private static Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("starting..");
        String port = args[0];
        ProtocolConnector connector = new EnoceanSerialConnector();
        connector.connect(port);
        ESP3Host esp3Host = new ESP3Host(connector);
    
        esp3Host.addDeviceProfile(EnoceanId.fromInt(0x0025A2DC), EEPId.EEP_F6_02_01);
        esp3Host.addDeviceProfile(EnoceanId.fromInt(0x0181DCDD), EEPId.EEP_D5_00_01);
        
        new Thread(esp3Host).start();         
        
// ++++++++++ TEST Start light actuator 2 ++++++++++++++
        final String ON = "ON";
        final String OFF = "OFF";
        final byte[] id={(byte)0x00, (byte)0x25 ,(byte)0xA2, (byte)0xDE};
        final EnoceanId enId= new EnoceanId(id);
//        final EnoceanParameterAddress parAddress = new EnoceanParameterAddress(enId);        
        StateChanger change = new StateChanger();
//        RLC rlc = new RLC(0x250);     
        
        BasicPacket packet = change.changeState(ON, enId, EEPId.EEP_F6_02_01.toString());       
        logger.info("Send packet: " + packet.toString());
        esp3Host.sendRadio(packet);
        
        // Switch actuator on and off every 500ms
//        for(int i=0;i<=20;i++){
//        	BasicPacket packet = change.changeState((i%2==0)?ON:OFF, enId, EEPId.EEP_F6_02_01.toString(),rlc.getByte());       
//            logger.info("Send packet: RLC " + Integer.toHexString(rlc.readValue()) + " Packet: " + packet.toString());
//            esp3Host.sendRadio(packet);
//            Thread.sleep(500);
//        }     
//++++++++++ TEST End light actuator 2 ++++++++++++++
   
// ++++++++++ TEST Start light actuator ++++++++++++++
//        RadioPacket radio;
//        
//        byte dataOn[] = {(byte) 0xF6, (byte) 0x50, (byte) 0x00, (byte) 0x25, (byte) 0xA2, (byte) 0xDC, (byte) 0x30};
//        byte optDataOn[] = {(byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x32, (byte) 0x00};
//        byte dataOff[] = {(byte) 0xF6, (byte) 0x70, (byte) 0x00, (byte) 0x25, (byte) 0xA2, (byte) 0xDC, (byte) 0x30};
//        byte optDataOff[] = {(byte) 0x01, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x3A, (byte) 0x00};   
//        
//        radio = new RadioPacket(dataOff, (byte) 0x01, 0xFFFFFFFF, (byte) 0x51, (byte) 0x00);
//        
//        BasicPacket packet = radio;        
//        logger.info("Send packet: " + packet.toString());
//        esp3Host.sendRadio(packet);
//        
// ++++++++++ TEST End light actuator ++++++++++++++
        
// ++++++++++ TEST Start heating actuator ++++++++++++++
//      RadioPacket4BS radio4BS;
//      
//      byte data[] = {(byte) 0x00, (byte) 0x00, (byte) 0x80, (byte) 0x10};
//      
//      // address of actuator 0x00857A08
//      radio4BS = new RadioPacket4BS(data, 0xAA25A2DC, (byte) 0x00, (byte) 0x03, 0x00857A08, (byte) 0xFF, (byte) 0x00);
//        
//       packet = radio4BS;
//      logger.info("Send packet: " + packet.toString());
//      esp3Host.sendRadio(packet);      
//++++++++++ TEST End heating actuator ++++++++++++++
      
   // ++++++++++ TEST Start general packet ++++++++++++++
//      BasicPacket packet = new QueryIdCommand();        
//      EventPacket packet = new 
//      Header h;
//      Payload p = new Payload();
//      RawPacket raw;
//      EventPacket event;  
        
//      h = new Header((byte)0x05, (short)0x01, (byte)0x00);        
        
//      esp3Host.start();
//      p.setData(dataOn);
//      p.setOptionalData(optDataOn);
//      p.initCRC8();
//      BasicPacket packet = new RadioPacket((new RawPacket(new Header((byte)0x01, (short)0x07, (byte)0x07), p)));
//      esp3Host.sendRadio(packet);
//++++++++++ TEST End general packet ++++++++++++++
    }
}
