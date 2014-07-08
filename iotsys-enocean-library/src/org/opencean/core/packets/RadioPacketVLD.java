package org.opencean.core.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * eep D2-01-08
 * 
 * VLD 0:4 CMD:4 0x7
 * 
 * UN:3 units I/O:5 channel
 * 
 * MV:32 value
 * 
 * @author Thomas Letsch (contact@thomas-letsch.de)
 * 
 */
public class RadioPacketVLD extends RadioPacket {
    private static Logger logger = LoggerFactory.getLogger(RadioPacketVLD.class);

    public static final byte RADIO_TYPE = (byte) 0xD2;
    private byte CMDByte;
    private byte UNByte;
    private byte IOByte;
    private long MValue; // formally need be unsigned int32
    private byte StateByte;

    public RadioPacketVLD(RawPacket rawPacket) {
        super(rawPacket);
    }

    @Override
    public void parseData() {
        super.parseData();
        CMDByte = (byte) (payload.getData()[1] & 0x0F);
        logger.info("VLD CMD = " + CMDByte);

        if (CMDByte == 0x7 && (payload.getData().length >= 7) && payload.getData()[2] == 0x60) {
            // actuator measurement response power
            UNByte = (byte) ((payload.getData()[2] & 0xE0) >> 5);
            IOByte = (byte) (payload.getData()[2] & 0x1F);
            MValue = payload.getData()[6] + (payload.getData()[5] << 8) + (payload.getData()[4] << 16) + (payload.getData()[3] << 24);
            logger.info("new VLD MV = " + MValue);

        } else if (CMDByte == 0x4 && (payload.getData().length >= 4)) {
            // actuator status response
            byte value = (byte) ((payload.getData()[3] & 0x7F));
            if (value <= 0x64) {
                StateByte = value;
            }
            logger.info("new VLD STATE = " + value);
        }
    }

    public byte getCMDByte() {
        return CMDByte;
    }

    public byte getUNByte() {
        return UNByte;
    }

    public byte getIOByte() {
        return IOByte;
    }

    public long getMValue() {
        return MValue;
    }

    public byte getStateByte() {
        return StateByte;
    }

}