package org.opencean.core.packets;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.utils.ByteArray;
import org.opencean.core.utils.CRC8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Header {
    private static Logger logger = LoggerFactory.getLogger(Header.class);

    private byte packetType;
    private short dataLength;
    private byte optionalDataLength;
    private byte crc8;

    public static Header from(ProtocolConnector connector) {
        logger.debug("Reading header...");
        Header header = new Header();
        header.setDataLength(connector.getShort());
        header.setOptionalDataLength(connector.get());
        header.setPacketType(connector.get());
        header.crc8 = connector.get();
        logger.debug(header.toString());
        return header;
    }

    public Header() {
    }

    public Header(byte packetType, short dataLength, byte optionalDataLength) {
        this.setPacketType(packetType);
        this.setDataLength(dataLength);
        this.setOptionalDataLength(optionalDataLength);
        this.crc8 = calculateCrc8();
    }

    public byte[] toBytes() {
        ByteArray bytes = new ByteArray();
        bytes.addShort(getDataLength());
        bytes.addByte(getOptionalDataLength());
        bytes.addByte(getPacketType());
        bytes.addByte(crc8);
        return bytes.getArray();
    }

    public boolean isValid() {
        return calculateCrc8() == crc8;
    }

    public void initCRC8() {
        crc8 = calculateCrc8();
    }

    public void checkCrc8() {
        if (calculateCrc8() != crc8) {
            throw new RuntimeException("Header CRC 8 is not correct! Expected " + calculateCrc8() + ", but received " + crc8);
        }
    }

    public byte getPacketType() {
        return packetType;
    }

    public short getDataLength() {
        return dataLength;
    }

    public byte getOptionalDataLength() {
        return optionalDataLength;
    }

    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }

    public void setDataLength(short dataLength) {
        this.dataLength = dataLength;
    }

    public void setOptionalDataLength(byte optionalDataLength) {
        this.optionalDataLength = optionalDataLength;
    }

    private byte[] getDataLengthBytes() {
        byte lowByte = (byte) (getDataLength() & 0xFF);
        byte highByte = (byte) ((getDataLength() >> 8) & 0xFF);
        return new byte[] { highByte, lowByte };
    }

    private byte calculateCrc8() {
        CRC8 crc8 = new CRC8();
        crc8.update(getDataLengthBytes()[0]);
        crc8.update(getDataLengthBytes()[1]);
        crc8.update(getOptionalDataLength());
        crc8.update(getPacketType());
        return (byte) crc8.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("Header: ");
        sb.append(String.format("dataLength=%02X", getDataLength()));
        sb.append(String.format(", optionalDataLength=%02X", getOptionalDataLength()));
        sb.append(String.format(", packetType=%02X", getPacketType()));
        sb.append(String.format(", crc8h=%02X", crc8));
        sb.append("]");
        return sb.toString();
    }

}
