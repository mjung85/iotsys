package org.opencean.core;

import org.opencean.core.common.ParameterAddress;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.values.Value;
import java.util.logging.Logger;

public class LoggingListener implements ParameterValueChangeListener {

    private static Logger logger = Logger.getLogger(LoggingListener.class.getName());

    @Override
    public void valueChanged(ParameterAddress parameterId, Value value) {
        logger.info("Received RadioPacket with value " + value);
    }

}
