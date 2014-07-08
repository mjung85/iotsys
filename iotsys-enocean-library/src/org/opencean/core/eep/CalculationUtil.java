package org.opencean.core.eep;

import java.math.BigDecimal;
import java.math.MathContext;

public class CalculationUtil {

    /**
     * Re-scales an input byte of a given range (rangeMin, rangeMax) to a given scale (scaleMin, scaleMax). Means if the input is the same
     * as rangeMax, it returns an output of scaleMax. If the input is the same as rangeMin, the output is scaleMin. Values between rangeMin
     * and rangeMax get returns in between scaleMin and scaleMax.
     */
    public BigDecimal rangeValue(byte input, double scaleMin, double scaleMax, double rangeMin, double rangeMax, int digits) {
        int rawValue = input & 0xFF;
        double multiplier = (scaleMax - scaleMin) / (rangeMax - rangeMin);
        return new BigDecimal(multiplier * (rawValue - rangeMin) + scaleMin, new MathContext(digits));
    }

}
