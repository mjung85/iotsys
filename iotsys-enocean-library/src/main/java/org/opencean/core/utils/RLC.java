package org.opencean.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RLC {
	private static Logger logger = LoggerFactory.getLogger(CircularByteBuffer.class);

	private int value = 0;
	private int overflow = 255;

	public RLC(int start) {
		value = start;
	}
	
	public RLC(int start, int max) {
		value = start;
		overflow = max;
	}
	
	public void setValue(int value){
		this.value = value;
	}
	
	public int readValue(){
		return value;
	}

	public int get() {		
		int temp = value;
		logger.info("RLC value=" +value);
		if(value < overflow){
			value++;
		} else {
			value=0;
		}		
		return temp;
	}	   

	public short getShort() {		
		short temp = (short)(0xFFFF&value);
		logger.info("RLC value=" +value);
		if(value < overflow){
			value++;
		} else {
			value=0;
		}
		return temp;
	}	
	
	public byte getByte() {		
		byte temp = (byte)(0xFF&value);
		logger.info("RLC value=" +value);
		if(value < overflow){
			value++;
		} else {
			value=0;
		}
		return temp;
	}

	public void setOverflow(int value) {
		overflow = value;
	}

	public int getOverflow() {
		return overflow;
	}

}
