package org.opencean.core.eep;

import java.util.HashMap;
import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.ButtonState;
import org.opencean.core.common.values.ContactState;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.BasicPacket;
import org.opencean.core.packets.RadioPacket1BS;
import org.opencean.core.utils.Bits;
import java.util.logging.Logger;

public class SingleInputContact implements EEPParser {
	
	private static Logger logger = Logger.getLogger(SingleInputContact.class.getName());
	
	private ContactState contact;
	private ButtonState learnButton;

    @Override
    public Map<EnoceanParameterAddress, Value> parsePacket(BasicPacket packet) {
        Map<EnoceanParameterAddress, Value> map = new HashMap<EnoceanParameterAddress, Value>();
        if (packet instanceof RadioPacket1BS) {
            RadioPacket1BS radioPacket1BS = (RadioPacket1BS) packet;
            contact = ContactState.values()[Bits.getBit(radioPacket1BS.getDataByte(), 0)];
            map.put(new EnoceanParameterAddress(radioPacket1BS.getSenderId(), Parameter.CONTACT_STATE), contact);
            learnButton = Bits.getBool(radioPacket1BS.getDataByte(), 3) ? ButtonState.RELEASED : ButtonState.PRESSED;
            map.put(new EnoceanParameterAddress(radioPacket1BS.getSenderId(), Parameter.LEARN_BUTTON), learnButton);
        }
        logger.info("Current State: " + this);
        return map;
    } 
    
    @Override
    public String toString() {
        return "Contact=" + contact + ", learnButton=" + learnButton;
    }
}
