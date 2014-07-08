package org.opencean.core.common;

import org.opencean.core.common.values.Value;

public interface ParameterValueChangeListener {

    void valueChanged(ParameterAddress parameterId, Value value);

}
