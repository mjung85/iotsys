package org.opencean.core.eep;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;

public class CalculationUtilTest {

    @Test
    public void testCalculateRangeValue() {
        BigDecimal value = new BigDecimal(10.2345, new MathContext(2));
        System.out.println(value.toString());
    }

}
