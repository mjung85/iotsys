package org.opencean.core.packets;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.utils.ByteArray;
import org.opencean.core.utils.CRC8;
import java.util.logging.Logger;

public class Payload {
    private static Logger logger = Logger.getLogger(Payload.class.getName());

    private byte[] data;
    private byte[] optionalData;
    private byte crc8;

    public static Payload from(Header header, ProtocolConnector connector) {
        logger.finest("Reading payload...");
        Payload payload = new Payload();
        payload.setData(new byte[header.getDataLength()]);
        connector.get(payload.getData());
        payload.setOptionalData(new byte[header.getOptionalDataLength()]);
        connector.get(payload.getOptionalData());
        payload.crc8 = connector.get();
        logger.finest(payload.toString());
        return payload;
    }

    public Payload() {
        data = new byte[] {};
        optionalData = new byte[] {};
    }

    public void initCRC8() {
        crc8 = calculateCrc8();
    }

    public boolean isValid() {
        return calculateCrc8() == crc8;
    }

    public void checkCrc8() {
        if (calculateCrc8() != crc8) {
            throw new RuntimeException("Payload CRC 8 is not correct! Expected " + calculateCrc8() + ", but received " + crc8);
        }
    }

    public byte[] toBytes() {
        ByteArray bytes = new ByteArray();
        bytes.addBytes(getData());
        bytes.addBytes(getOptionalData());
        bytes.addByte(crc8);
        return bytes.getArray();
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getOptionalData() {
        return optionalData;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setOptionalData(byte[] optionalData) {
        this.optionalData = optionalData;
    }

    @Override
    public String toString() {
        return "Payload: " + "data=" + new ByteArray(getData()) + ", optionaldata=" + new ByteArray(getOptionalData()) + ", crc8d=" + crc8;
    }
    
    private byte calculateCrc8() {
        CRC8 crc8 = new CRC8();
        if (data != null) {
            crc8.update(data, 0, data.length);
        }
        if (optionalData != null) {
            crc8.update(optionalData, 0, optionalData.length);
        }
        return (byte) crc8.getValue();
    }

}
