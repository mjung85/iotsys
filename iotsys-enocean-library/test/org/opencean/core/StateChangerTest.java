package org.opencean.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.EEPId;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket;

/**
 * "55, 00, 07, 07, 01, 7A, F6, 50, 00, 00, 00, 00, 30, 03, FF, FF, FF, FF, FF, 00, E8"
 * 
 * @author Jürgen Schober
 * 
 */
public class StateChangerTest { 
	
    @Test
    public void testStateChangerOn() {
    	final byte packet[]={(byte)0x55, (byte)0x00, (byte)0x07, (byte)0x07, (byte)0x01, (byte)0x7A, (byte)0xF6, (byte)0x50, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x30, (byte)0x03, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0xE8};
    	byte createdPacket[];
    	StateChanger change = new StateChanger();
		BasicPacket basicPacket = change.changeState("ON", EnoceanId.fromString("00:00:00:00"), EEPId.EEP_F6_02_01.toString());		
	    
    	RadioPacket radioPacket = (RadioPacket) basicPacket;    
    	createdPacket = radioPacket.toBytes();
    	for(int i=0;i<packet.length;i++){
    		assertEquals(packet[i], createdPacket[i]); 
    	}
    	  	        
    }    
    
    @Test
    public void testStateChangerOff() {
    	final byte packet[]={(byte)0x55, (byte)0x00, (byte)0x07, (byte)0x07, (byte)0x01, (byte)0x7A, (byte)0xF6, (byte)0x70, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x30, (byte)0x03, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x00, (byte)0x16};
    	byte createdPacket[];
    	StateChanger change = new StateChanger();
		BasicPacket basicPacket = change.changeState("OFF", EnoceanId.fromString("00:00:00:00"), EEPId.EEP_F6_02_01.toString());		
	    
    	RadioPacket radioPacket = (RadioPacket) basicPacket;    
    	createdPacket = radioPacket.toBytes();
    	for(int i=0;i<packet.length;i++){
    		assertEquals(packet[i], createdPacket[i]); 
    	}
    	  	        
    } 
   
}
