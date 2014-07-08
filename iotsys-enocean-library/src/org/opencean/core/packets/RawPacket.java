package org.opencean.core.packets;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.utils.ByteArray;

public class RawPacket {

    private Header header;

    private Payload payload;

    public RawPacket() {
    }

    public RawPacket(Header header, Payload payload) {
        this.header = header;
        this.payload = payload;
    }

    public void readHeader(ProtocolConnector connector) {
        header = Header.from(connector);
    }

    public void readPayload(ProtocolConnector connector) {
        payload = Payload.from(header, connector);
    }

    public byte[] toBytes() {
        ByteArray bytes = new ByteArray();
        bytes.addBytes(header.toBytes());
        bytes.addBytes(payload.toBytes());
        return bytes.getArray();
    }

    public Header getHeader() {
        return header;
    }

    public Payload getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return new ByteArray(toBytes()).toString();
    }

}
