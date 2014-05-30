package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket1BS;

public abstract class RadioPacket1BSParser implements EEPParser {

    protected abstract void parsePacket(Map<EnoceanParameterAddress, Value> values, RadioPacket1BS packet);

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket1BS) {
            RadioPacket1BS radioPacket1BS = (RadioPacket1BS) packet;
            parsePacket(map, radioPacket1BS);
        }
        return map;
    }

}
