package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket4BS;

public abstract class RadioPacket4BSParser implements EEPParser {

    protected abstract void parsePacket(Map<EnoceanParameterAddress, Value> values, RadioPacket4BS packet);

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
            parsePacket(map, radioPacket4BS);
        }
        return map;
    }

}
