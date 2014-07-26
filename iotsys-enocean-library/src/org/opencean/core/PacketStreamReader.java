package org.opencean.core;

import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RawPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketStreamReader {

    private static Logger logger = LoggerFactory.getLogger(PacketStreamReader.class);

    private ProtocolConnector connector;

    public PacketStreamReader(ProtocolConnector connector) {
        this.connector = connector;
    }

    /**
     * Waits for a sync byte first. Then checks the read header CRC8 and resets
     * the connector to the state after the last sync byte.
     * 
     * @return The received packet or null if header was incorrect (and that
     *         means no packet start was recognized)
     */
    public BasicPacket read() {
        seekTillSyncByte();
        connector.mark();
        RawPacket rawPacket = new RawPacket();
        rawPacket.readHeader(connector);
        if (!rawPacket.getHeader().isValid()) {
            connector.reset();
            logger.debug("Header not valid. Resetting.");
            return null;
        }
        rawPacket.readPayload(connector);
        if (!rawPacket.getPayload().isValid()) {
            logger.warn("Payload CRC not correct! Package received: " + rawPacket);
            connector.reset();
            logger.debug("Payload not valid. Resetting.");
            return null;
        }
        BasicPacket packet = PacketFactory.createFrom(rawPacket);
        return packet;
    }

    private void seekTillSyncByte() {
        while (!(connector.get() == BasicPacket.SYNC_BYTE)) {
            logger.debug("Waiting for sync byte");
        }
    }

}
