package org.opencean.core.eep;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;

public interface EEPSerializer {

    BasicPacket createPacket(EnoceanParameterAddress parameterAddress, Value value);

}
