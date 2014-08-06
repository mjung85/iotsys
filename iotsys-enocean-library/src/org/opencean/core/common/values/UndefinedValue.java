package org.opencean.core.common.values;

public class UndefinedValue implements Value {

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public String getDisplayValue() {
        return "UNDEFINED";
    }
}
