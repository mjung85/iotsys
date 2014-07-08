package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacketRPS;

public abstract class RadioPacketRPSParser implements EEPParser {
	protected abstract void parsePacket(Map<EnoceanParameterAddress, Value> values, RadioPacketRPS radioPacket);

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacketRPS) {
            RadioPacketRPS radioPacketRPS = (RadioPacketRPS) packet;
            parsePacket(map, radioPacketRPS);    
        }
        return map;
    } 
}
