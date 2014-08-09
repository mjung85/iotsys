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
 * EltakoLumSensor. FAH60+FAH63+FIH63 Luminosity Sensor parser for Eltako
 * product. EEP id is an old version deprecated in EEP 2.5 specs
 * 
 * spec from Eltako_wireless_system_chapterT_high_res.pdf
 * 
 * FAH60+FAH63+FIH63 (EEP: 07-06-01 plus Data_byte3) ORG = 0x07 Data_byte3 =
 * brightness 0 - 100 lux, linear n = 0x00 - 0xFF (only valid if DB2 = 0x00)
 * Data_byte2 = brightness 300 - 30.000 lux, linear n = 0x00 - 0xFF Data_byte1 =
 * - Data_byte0 = DB0_Bit3 = LRN Button (0 = teach-in telegram, 1 = data
 * telegram) for data telegram: 0x0F, for teach-in telegram: 0x87 Teach-in
 * telegram BD3..DB0: 0x18, 0x08, 0x0D, 0x87
 * 
 * 
 * @author Nicolas Bonnefond INRIA
 */
public class EltakoLumSensor implements EEPParser {

    private static final float BYTE_RANGE_MIN = 0f;

    private static final float BYTE_RANGE_MAX = 255f;

    private static final float SCALE_MIN = 300f;

    private static final float SCALE_MAX = 30000f;

    private static final float SMALLER_SCALE_MIN = 0f;

    private static final float SMALLER_SCALE_MAX = 100f;

    public static final String MANUFACTURER_ID = "16#00D";

    private float scaleMin;

    private float scaleMax;

    private float currentValue;

    private float smallerScaleMin;

    private float smallerScaleMax;

    /**
     * Instantiates a new eltako luminosity sensor with FAH60+FAH63+FIH63 scales
     */
    public EltakoLumSensor() {
        this.scaleMin = SCALE_MIN;
        this.scaleMax = SCALE_MAX;
        this.smallerScaleMin = SMALLER_SCALE_MIN;
        this.smallerScaleMax = SMALLER_SCALE_MAX;
    }

    /**
     * Instantiates a new eltako luminosity sensor
     * 
     * @param scaleMin
     *            the scale min for DB2 parsing
     * @param scaleMax
     *            the scale max for DB2 parsing
     * @param smallerScaleMin
     *            the smaller scale min for DB3 parsing
     * @param smallerScaleMax
     *            the smaller scale max for DB3 parsing
     */
    public EltakoLumSensor(float scaleMin, float scaleMax, float smallerScaleMin, float smallerScaleMax) {
        this.scaleMin = scaleMin;
        this.scaleMax = scaleMax;
        this.smallerScaleMin = smallerScaleMin;
        this.smallerScaleMax = smallerScaleMax;
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
     * Parses DB2 for luminosity with scale min and max If DB2 parsing returns 0
     * parses DB3 with smaller scale min and max
     * 
     */
    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
            byte source = radioPacket4BS.getDb2();

            if (source != (byte) 0x00) {
                this.currentValue = this.calculateValue(source, this.scaleMin, this.scaleMax, BYTE_RANGE_MIN, BYTE_RANGE_MAX);
            } else {
                this.currentValue = this.calculateValue(radioPacket4BS.getDb3(), this.smallerScaleMin, this.smallerScaleMax,
                        BYTE_RANGE_MIN, BYTE_RANGE_MAX);
            }

            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.ILLUMINANCE), new NumberWithUnit(Unit.LUX,
                    (int) this.currentValue));
        }
        return map;
    }

}
