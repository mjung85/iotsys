package org.opencean.core.common.values;

public class NumberWithUnit implements Value {

    private Unit unit;

    private Number value;

    public NumberWithUnit(Unit unit, Number value) {
        this.unit = unit;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getDisplayValue() {
        return value + "" + unit;
    }

    @Override
    public String toString() {
        return getDisplayValue();
    }
}
