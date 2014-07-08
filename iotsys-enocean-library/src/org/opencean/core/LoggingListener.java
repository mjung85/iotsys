package org.opencean.core;

import org.opencean.core.common.ParameterAddress;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.values.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingListener implements ParameterValueChangeListener {

    private static Logger logger = LoggerFactory.getLogger(ESP3Host.class);

    @Override
    public void valueChanged(ParameterAddress parameterId, Value value) {
        logger.info("Received RadioPacket with value " + value);
    }

}
