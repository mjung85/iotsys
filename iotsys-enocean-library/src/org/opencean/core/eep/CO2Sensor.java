package org.opencean.core.eep;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.NumberWithUnit;
import org.opencean.core.common.values.Unit;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket4BS;

/**
 * Parser for official implementation of a CO2 sensor returns CO2, humidity and
 * temp
 * 
 * @author Nicolas Bonnefond INRIA
 */
public class CO2Sensor implements EEPParser {

    public static final EEPId EEP_ID = new EEPId("A5:09:04");

    public static int OFFICIAL_SCALE_MIN = 0;
    public static int OFFICIAL_SCALE_MAX_HUMIDITY = 100;
    public static int OFFICIAL_SCALE_MAX_C02 = 2250;
    public static int OFFICIAL_SCALE_MAX_TEMP = 51;
    private static final int OFFICIAL_BYTE_RANGE_MIN = 0;
    private static final int OFFICIAL_BYTE_RANGE_MAX_CO2_TEMP = 255;
    private static final int OFFICIAL_BYTE_RANGE_MAX_HUMIDITY = 200;

    private int rangeMinCO2;
    private int rangeMaxCO2;
    private int rangeMinTemp;
    private int rangeMaxTemp;
    private int rangeMinHumidity;
    private int rangeMaxHumidity;
    private int scaleMinCO2;
    private int scaleMaxCO2;
    private int scaleMinTemp;
    private int scaleMaxTemp;
    private int scaleMinHumidity;
    private int scaleMaxHumidity;

    private CalculationUtil calculationUtil = new CalculationUtil();

    /**
     * Instantiates a new official co2 sensor with official enocean spec scales
     */
    public CO2Sensor() {
        rangeMaxCO2 = OFFICIAL_BYTE_RANGE_MAX_CO2_TEMP;
        rangeMinCO2 = OFFICIAL_BYTE_RANGE_MIN;
        rangeMaxHumidity = OFFICIAL_BYTE_RANGE_MAX_HUMIDITY;
        rangeMinHumidity = OFFICIAL_BYTE_RANGE_MIN;
        rangeMaxTemp = OFFICIAL_BYTE_RANGE_MAX_CO2_TEMP;
        rangeMinTemp = OFFICIAL_BYTE_RANGE_MIN;

        scaleMaxCO2 = OFFICIAL_SCALE_MAX_C02;
        scaleMinCO2 = OFFICIAL_SCALE_MIN;
        scaleMaxHumidity = OFFICIAL_SCALE_MAX_HUMIDITY;
        scaleMinHumidity = OFFICIAL_SCALE_MIN;
        scaleMaxTemp = OFFICIAL_SCALE_MAX_TEMP;
        scaleMinTemp = OFFICIAL_SCALE_MIN;
    }

    /**
     * Instantiates a new official c o2 sensor.
     * 
     * @param rangeMinCO2
     *            the range min c o2
     * @param rangeMaxCO2
     *            the range max c o2
     * @param rangeMinTemp
     *            the range min temp
     * @param rangeMaxTemp
     *            the range max temp
     * @param rangeMinHumidity
     *            the range min humidity
     * @param rangeMaxHumidity
     *            the range max humidity
     * @param scaleMinCO2
     *            the scale min c o2
     * @param scaleMaxCO2
     *            the scale max c o2
     * @param scaleMinTemp
     *            the scale min temp
     * @param scaleMaxTemp
     *            the scale max temp
     * @param scaleMinHumidity
     *            the scale min humidity
     * @param scaleMaxHumidity
     *            the scale max humidity
     */
    public CO2Sensor(int rangeMinCO2, int rangeMaxCO2, int rangeMinTemp, int rangeMaxTemp, int rangeMinHumidity, int rangeMaxHumidity,
            int scaleMinCO2, int scaleMaxCO2, int scaleMinTemp, int scaleMaxTemp, int scaleMinHumidity, int scaleMaxHumidity) {

        this.rangeMinCO2 = rangeMinCO2;
        this.rangeMaxCO2 = rangeMaxCO2;
        this.rangeMinTemp = rangeMinTemp;
        this.rangeMaxTemp = rangeMaxTemp;
        this.rangeMinHumidity = rangeMinHumidity;
        this.rangeMaxHumidity = rangeMaxHumidity;
        this.scaleMinCO2 = scaleMinCO2;
        this.scaleMaxCO2 = scaleMaxCO2;
        this.scaleMinTemp = scaleMinTemp;
        this.scaleMaxTemp = scaleMaxTemp;
        this.scaleMinHumidity = scaleMinHumidity;
        this.scaleMaxHumidity = scaleMaxHumidity;
    }

    /**
     * Parses DB2 for CO2 , DB3 for Humidity , DB1 for Temperature
     */
    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;
            BigDecimal co2 = calculationUtil.rangeValue(radioPacket4BS.getDb2(), scaleMinCO2, scaleMaxCO2, rangeMinCO2, rangeMaxCO2, 4);
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.CO2_CONCENTRATION), new NumberWithUnit(Unit.PPM,
                    co2));
            BigDecimal temperature = calculationUtil.rangeValue(radioPacket4BS.getDb1(), scaleMinTemp, scaleMaxTemp, rangeMinTemp,
                    rangeMaxTemp, 3);
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.TEMPERATURE), new NumberWithUnit(
                    Unit.DEGREE_CELSIUS, temperature));
            BigDecimal humidity = calculationUtil.rangeValue(radioPacket4BS.getDb2(), scaleMinHumidity, scaleMaxHumidity, rangeMinHumidity,
                    rangeMaxHumidity, 3);
            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.HUMIDITY), new NumberWithUnit(Unit.HUMIDITY,
                    humidity));
        }
        return map;
    }

}
