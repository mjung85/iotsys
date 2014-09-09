package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.common.EEPId;

public class EEPParserFactory {

    private Map<EEPId, EEPParser> parser = new HashMap<EEPId, EEPParser>();

    public EEPParserFactory() {
        parser.put(EEPId.EEP_D2_01_08, new PowerSensor(EEPId.EEP_D2_01_08));
        parser.put(EEPId.EEP_A5_02_05, new TemperaturSensor(0, 40, EEPId.EEP_A5_02_05));
        parser.put(EEPId.EEP_A5_08_02, new LightTempertureAndOccupancySensor());
        parser.put(EEPId.EEP_A5_07_03, new OccupancySensor());
        parser.put(EEPId.EEP_F6_02_01, new RockerSwitch());
        parser.put(EEPId.EEP_F6_02_02, new RockerSwitch());
        parser.put(EEPId.EEP_D5_00_01, new SingleInputContact());
        parser.put(EEPId.EEP_07_06_01, new EltakoLumSensor());
        parser.put(CO2Sensor.EEP_ID, new CO2Sensor());
        parser.put(EEPId.EEP_A5_09_05, new VOCSensor());
    }

    public EEPParser getParserFor(EEPId profile) {
        return parser.get(profile);
    }

}
