package org.opencean.core.eep;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
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
 * Parser for official implementation of VOC sensor Returns VOC concentration
 * with ID of the gaz
 * 
 * @author Nicolas Bonnefond INRIA
 */
public class VOCSensor implements EEPParser {
    private static final float OFFICIAL_CONCENTRATION_BYTE_RANGE_MIN = 0f;

    private static final float OFFICIAL_CONCENTRATION_BYTE_RANGE_MAX = 65535f;

    private static final float OFFICIAL_CONCENTRATION_SCALE_MIN = 0f;

    private static final float OFFICIAL_CONCENTRATION_SCALE_MAX = 65535f;

    private static final Map<Integer, Float> SCALE_MULTIPLIER_MAP = Collections.unmodifiableMap(new HashMap<Integer, Float>() {
        private static final long serialVersionUID = 1L;

        {
            this.put(0, 0.01f);
            this.put(1, 0.1f);
            this.put(2, 1f);
            this.put(3, 10f);
        }
    });

    private static final Map<Integer, String> IDENTIFICATION_MAP = Collections.unmodifiableMap(new HashMap<Integer, String>() {
        private static final long serialVersionUID = 1L;

        {
            this.put(0, "VOCT_total");
            this.put(1, "Formaldehyde");
            this.put(2, "Benzene");
            this.put(3, "Styrene");
            this.put(4, "Toluene");
            this.put(5, "Tetrachloroethylene");
            this.put(6, "Xylene");
            this.put(7, "n-Hexane");
            this.put(8, "n-Octane");
            this.put(9, "Cyclopentane");
            this.put(10, "Methanol");
            this.put(11, "Ethanol");
            this.put(12, "1-Pentanol");
            this.put(13, "Acetone");
            this.put(14, "ethylene_Oxide");
            this.put(15, "Acetaldehyde_ue");
            this.put(16, "Acetic_Acid");
            this.put(17, "Propionice_Acid");
            this.put(18, "Valeric_Acid");
            this.put(19, "Butyric_Acid");
            this.put(20, "Ammoniac");
            this.put(22, "Hydrogen_Sulfide");
            this.put(23, "Dimethylsulfide");
            this.put(24, "2-Butanol_butyl_Alcohol");
            this.put(25, "2-Methylpropanol");
            this.put(26, "Diethyl_ether");
            this.put(255, "ozone");

        }
    });

    private float scaleMin;

    private float scaleMax;

    private float currentValueConcentration;

    private String currentValueIdentification;

    private float currentValueScaleMultiplier;

    /**
     * Instantiates a new official voc sensor with official scale values
     */
    public VOCSensor() {
        this.scaleMin = OFFICIAL_CONCENTRATION_SCALE_MIN;
        this.scaleMax = OFFICIAL_CONCENTRATION_SCALE_MAX;
    }

    /**
     * Calculates linear value of raw value in the scale
     * 
     * @param rawValue
     *            the raw value
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
    private float calculateValue(int rawValue, float scaleMin, float scaleMax, float byteRangeMin, float byteRangeMax) {

        float multiplier = (scaleMax - scaleMin) / (byteRangeMax - byteRangeMin);
        return multiplier * (rawValue - byteRangeMin) + scaleMin;
    }

    /**
     * Gets the gaz identification.
     * 
     * @param key
     *            the enum parsed
     * @return the gaz identification
     */
    private String getIdentification(int key) {
        if (IDENTIFICATION_MAP.containsKey(key)) {
            return IDENTIFICATION_MAP.get(key);
        } else {
            return "Unknown_id";
        }
    }

    /**
     * Gets the scale multiplier.
     * 
     * @param key
     *            the enum parsed
     * @return the scale multiplier
     */
    private Float getScaleMultiplier(int key) {
        if (SCALE_MULTIPLIER_MAP.containsKey(key)) {
            return SCALE_MULTIPLIER_MAP.get(key);
        } else {
            return -1f;
        }
    }

    /**
     * Parses DB3 + DB2 for Concentration value Parses DB1 for gaz identifier
     * Parses DB0 first 2 bits for scale multiplier
     * 
     * Sets concentration value * scale multiplier as value Adds gaz
     * identification in the parameter ID
     * 
     */
    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket4BS) {
            RadioPacket4BS radioPacket4BS = (RadioPacket4BS) packet;

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4);
            byteBuffer.order(ByteOrder.BIG_ENDIAN);
            byteBuffer.put((byte) 0x00);
            byteBuffer.put((byte) 0x00);
            byteBuffer.put(radioPacket4BS.getDb3());
            byteBuffer.put(radioPacket4BS.getDb2());
            byteBuffer.flip();

            int rawValue = byteBuffer.getInt() & 0xFFFF;

            this.currentValueConcentration = this.calculateValue(rawValue, this.scaleMin, this.scaleMax,
                    OFFICIAL_CONCENTRATION_BYTE_RANGE_MIN, OFFICIAL_CONCENTRATION_BYTE_RANGE_MAX);

            this.currentValueIdentification = this.getIdentification(radioPacket4BS.getDb1() & 0xFF);

            this.currentValueScaleMultiplier = this.getScaleMultiplier(radioPacket4BS.getDb0() & 0x03);

            map.put(new EnoceanParameterAddress(radioPacket4BS.getSenderId(), Parameter.VOC_CONCENTRATION + "_"
                    + this.currentValueIdentification), new NumberWithUnit(Unit.PPB, this.currentValueConcentration
                    * this.currentValueScaleMultiplier));
        }
        return map;
    }

}
