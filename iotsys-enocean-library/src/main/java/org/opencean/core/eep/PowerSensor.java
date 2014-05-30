package org.opencean.core.eep;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.ContactState;
import org.opencean.core.common.values.NumberWithUnit;
import org.opencean.core.common.values.Unit;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacketVLD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PowerSensor implements EEPParser {

    private static Logger logger = LoggerFactory.getLogger(PowerSensor.class);
    private BigDecimal currentValue;
    private EEPId eep;

    public PowerSensor(EEPId eep) {
        this.eep = eep;
    }

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        logger.info("Power eep " + eep.getId());
        if (packet instanceof RadioPacketVLD) {
            RadioPacketVLD radioPacketVLD = (RadioPacketVLD) packet;
            if (radioPacketVLD.getCMDByte() == 0x07) {
                currentValue = new BigDecimal(radioPacketVLD.getMValue(), new MathContext(3));
                map.put(new EnoceanParameterAddress(radioPacketVLD.getSenderId(), Parameter.POWER), new NumberWithUnit(Unit.WATT,
                        currentValue));
            } else if (radioPacketVLD.getCMDByte() == 0x04) {
                byte value = radioPacketVLD.getStateByte();
                if (value != 0) {
                    value = 1; // any on state == ON
                }
                ContactState contact = ContactState.values()[value];
                logger.info("State " + value + " " + contact.toString());
                map.put(new EnoceanParameterAddress(radioPacketVLD.getSenderId(), Parameter.CONTACT_STATE), contact);
            }
        }
        return map;
    }
}