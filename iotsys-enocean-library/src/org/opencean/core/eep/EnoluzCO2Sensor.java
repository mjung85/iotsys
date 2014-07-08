package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.NumberWithUnit;
import org.opencean.core.common.values.Unit;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket4BS;

/**
 * Parser for a simple CO2 sensor with no humidity or temperature information
 * based on enoluz product
 * 
 * spec from enoluz_co2_enocean_868_230Vac_datasheet.pdf
 * 
 * EnOcean EEP profile A5:09:04 Databyte 2(scale 0-255) 0: 400 ppm 255: 2550 ppm
 * 
 * Scale is different from official spec (0ppm...2550ppm) (and called range on
 * datasheet)
 * 
 * @author Nicolas Bonnefond INRIA
 */
public class EnoluzCO2Sensor implements EEPParser {

    private static final float BYTE_RANGE_MIN = 0;

    private static final float BYTE_RANGE_MAX = 255f;

    private float scaleMin;

    private float scaleMax;

    private float currentValue;

    /**
     * Instantiates a new enoluz simple co2 sensor.
     * 
     * @param scaleMin
     *            the scale min
     * @param scaleMax
     *            the scale max
     */
    public EnoluzCO2Sensor(float scaleMin, float scaleMax) {
        this.scaleMin = scaleMin;
        this.scaleMax = scaleMax;
    }

    /**
     * Calculates linear value of byte in the scale
     * 
     * @param source
     *            the source
     * @param scaleMin
     *            the scale min
     * @param scaleMax
     *            the scale max
     * @param byteRangeMin
     *            the byte range min
     * @param byteRangeMax
     *            the byte range max
     * @return the value in the scale
     */
    private float calculateValue(byte source, float scaleMin, float scaleMax, float byteRangeMin, float byteRangeMax) {
        int rawValue = source & 0xFF;

        float multiplier = (scaleMax - scaleMin) / (byteRangeMax - byteRangeMin);
        return multiplier * (rawValue - byteRangeMin) + scaleMin;
    }

    /**
     * Parses DB2 for CO2 concentration
     */
    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
            byte source = radioPacket4BS.getDb2();

            this.currentValue = this.calculateValue(source, this.scaleMin, this.scaleMax, BYTE_RANGE_MIN, BYTE_RANGE_MAX);

            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.CO2_CONCENTRATION), new NumberWithUnit(Unit.PPM,
                    (int) this.currentValue));
        }
        return map;
    }

}
