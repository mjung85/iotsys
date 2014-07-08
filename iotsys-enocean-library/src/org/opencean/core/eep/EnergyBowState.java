package org.opencean.core.eep;

import org.opencean.core.common.values.Value;

public enum EnergyBowState implements Value {
    RELEASED, PRESSED;

    @Override
    public String toString() {
        return getDisplayValue();
    }

    @Override
    public Object getValue() {
        return name();
    }

    @Override
    public String getDisplayValue() {
        return (this.equals(PRESSED) ? "Pressed" : "Released");
    }
}