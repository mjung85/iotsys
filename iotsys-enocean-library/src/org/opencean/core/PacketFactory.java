package org.opencean.core;

import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket;
import org.opencean.core.packets.RadioPacket1BS;
import org.opencean.core.packets.RadioPacket4BS;
import org.opencean.core.packets.RadioPacketRPS;
import org.opencean.core.packets.RadioPacketVLD;
import org.opencean.core.packets.RawPacket;
import org.opencean.core.packets.ResponsePacket;
import org.opencean.core.packets.UnknownPacket;

public class PacketFactory {

    public static BasicPacket createFrom(RawPacket rawPacket) {
        switch (rawPacket.getHeader().getPacketType()) {
        case RadioPacket.PACKET_TYPE:
            return createRadioPacket(rawPacket);
        case ResponsePacket.PACKET_TYPE:
            return new ResponsePacket(rawPacket);
        default:
            return new UnknownPacket();
        }
    }

    private static RadioPacket createRadioPacket(RawPacket rawPacket) {
        switch (rawPacket.getPayload().getData()[0]) {
        case RadioPacket1BS.RADIO_TYPE:
            return new RadioPacket1BS(rawPacket);
        case RadioPacketRPS.RADIO_TYPE:
            return new RadioPacketRPS(rawPacket);
        case RadioPacket4BS.RADIO_TYPE:
            return new RadioPacket4BS(rawPacket);
        case RadioPacketVLD.RADIO_TYPE:
            return new RadioPacketVLD(rawPacket);
        default:
            return new RadioPacket(rawPacket);
        }
    }

}
