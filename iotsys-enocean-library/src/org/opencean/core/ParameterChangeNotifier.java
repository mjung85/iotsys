package org.opencean.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.values.Value;
import org.opencean.core.eep.EEPParser;
import org.opencean.core.eep.EEPParserFactory;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket;
import java.util.logging.Logger;

public class ParameterChangeNotifier implements EnoceanReceiver {

    private static Logger logger = Logger.getLogger(ParameterChangeNotifier.class.getName());

    private List<ParameterValueChangeListener> valueChangeListeners = new ArrayList<ParameterValueChangeListener>();
    private Map<EnoceanId, EEPId> deviceToEEP = new HashMap<EnoceanId, EEPId>();
    private EEPParserFactory parserFactory = new EEPParserFactory();

    public void addDeviceProfile(EnoceanId id, EEPId epp) {
        deviceToEEP.put(id, epp);
    }

    public void addParameterValueChangeListener(ParameterValueChangeListener listener) {
        valueChangeListeners.add(listener);
    }

    public void removeParameterValueChangeListener(ParameterValueChangeListener listener) {
        valueChangeListeners.remove(listener);
    }

    @Override
    public void receivePacket(BasicPacket packet) {
        if (packet instanceof RadioPacket) {
            RadioPacket radioPacket = (RadioPacket) packet;
            Map<EnoceanParameterAddress, Value> values = retrieveValue(radioPacket);
            for (EnoceanParameterAddress address : values.keySet()) {
                for (ParameterValueChangeListener listener : valueChangeListeners) {
                    listener.valueChanged(address, values.get(address));
                }
            }
        }
    }

    private Map<EnoceanParameterAddress, Value> retrieveValue(RadioPacket radioPacket) {
        if (deviceToEEP.containsKey(radioPacket.getSenderId())) {
            EEPId profile = deviceToEEP.get(radioPacket.getSenderId());
            EEPParser parser = parserFactory.getParserFor(profile);
            if (profile != null && parser != null) {
                return parser.parsePacket(radioPacket);
            } else {
                logger.info("Device with id=" + radioPacket.getSenderId() + " and eep=" + profile
                        + " is not properly configured or not supported.");
            }
        }
        return new HashMap<EnoceanParameterAddress, Value>();
    }

}
