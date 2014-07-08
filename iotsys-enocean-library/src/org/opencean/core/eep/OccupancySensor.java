package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.NumberWithUnit;
import org.opencean.core.common.values.OnOffState;
import org.opencean.core.common.values.Unit;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.LearnButtonState;
import org.opencean.core.packets.RadioPacket4BS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccupancySensor implements EEPParser {

    private static Logger logger = LoggerFactory.getLogger(OccupancySensor.class);

    private LearnButtonState learnButton;

    private CalculationUtil calculationUtil = new CalculationUtil();

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
            byte db0 = radioPacket4BS.getDb0();
            byte db1 = radioPacket4BS.getDb1();
            byte db2 = radioPacket4BS.getDb2();
            byte db3 = radioPacket4BS.getDb3();
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.POWER), new NumberWithUnit(Unit.VOLTAGE,
                    calculationUtil.rangeValue(db3, 0, 5.0, 0, 250, 2)));

            // TODO: This is wrongly calculated. See EEP for details
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.ILLUMINANCE), new NumberWithUnit(Unit.LUX,
                    calculationUtil.rangeValue(db2, 0, 1020, 0, 255, 4)));
            OnOffState movement = OnOffState.values()[(db0 & 0x80) >> 7];
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.MOVEMENT), movement);
            learnButton = LearnButtonState.values()[(db0 & 0x08) >> 3];
        }
        return map;
    }

}
