package org.opencean.core.eep;

import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;

public interface EEPParser {

    Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet);

}