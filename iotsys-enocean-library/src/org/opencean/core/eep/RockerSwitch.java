package org.opencean.core.eep;

import java.util.Map;

import org.opencean.core.address.EnoceanParameterAddress;
import org.opencean.core.common.Parameter;
import org.opencean.core.common.values.ButtonState;
import org.opencean.core.common.values.Value;
import org.opencean.core.packets.RadioPacketRPS;
import org.opencean.core.utils.Bits;
import java.util.logging.Logger;

public class RockerSwitch extends RadioPacketRPSParser {

    private static Logger logger = Logger.getLogger(RockerSwitch.class.getName());

    public static final String CHANNEL_A = "A";
    public static final String CHANNEL_B = "B";

    private NUState nu;
    private T21State t21;

    private ButtonState buttonAO;
    private ButtonState buttonAI;
    private ButtonState buttonBO;
    private ButtonState buttonBI;
    private EnergyBowState energyBow;

    @Override
    protected void parsePacket(Map<EnoceanParameterAddress, Value> values, RadioPacketRPS radioPacket) {
    	byte statusByte = radioPacket.getStatus();
        byte dataByte = radioPacket.getDataByte();
        energyBow = EnergyBowState.values()[(dataByte & 0x10) >> 4];
        nu = NUState.values()[(statusByte & 0x10) >> 4];
        t21 = T21State.values()[(statusByte & 0x20) >> 5];
        if (energyBow.equals(EnergyBowState.RELEASED)) {
            releaseButton();
            addButtonStateToParameters(values, radioPacket);
        } else {
            if (NUState.UNASSIGNEDMESSAGE.equals(nu)) {
                logger.info("NU = 0 => unassigned pressed button message received. Not supported!");
                return;
            }
            resetButtons();
            byte rocker1 = (byte) ((dataByte & 0xE0) >> 5);
            parseButtonStates(rocker1);
            addButtonStateToParameters(values, radioPacket);
            boolean secondAction = Bits.isBitSet(dataByte, 0);
            if (secondAction) {
                logger.info("Second action received for id " + radioPacket.getSenderId() + ". Not expected.");
                resetButtons();
                byte rocker2 = (byte) ((dataByte & 0x0E) >> 1);
                parseButtonStates(rocker2);
                addButtonStateToParameters(values, radioPacket);
            }
        }       
        logger.info("Current State: " + this);
    }  

    private void addButtonStateToParameters(Map<EnoceanParameterAddress, Value> map, RadioPacketRPS radioPacketRPS) {
        if (buttonAO != null) {
            map.put(new EnoceanParameterAddress(radioPacketRPS.getSenderId(), CHANNEL_A, Parameter.O), buttonAO);
        }
        if (buttonAI != null) {
            map.put(new EnoceanParameterAddress(radioPacketRPS.getSenderId(), CHANNEL_A, Parameter.I), buttonAI);
        }
        if (buttonBO != null) {
            map.put(new EnoceanParameterAddress(radioPacketRPS.getSenderId(), CHANNEL_B, Parameter.O), buttonBO);
        }
        if (buttonBI != null) {
            map.put(new EnoceanParameterAddress(radioPacketRPS.getSenderId(), CHANNEL_B, Parameter.I), buttonBI);
        }
    }

    private void resetButtons() {
        buttonAO = null;
        buttonBO = null;
        buttonAI = null;
        buttonBI = null;
    }

    private void releaseButton() {
        if (buttonAO != null) {
            buttonAO = ButtonState.RELEASED;
        }
        if (buttonAI != null) {
            buttonAI = ButtonState.RELEASED;
        }
        if (buttonBO != null) {
            buttonBO = ButtonState.RELEASED;
        }
        if (buttonBI != null) {
            buttonBI = ButtonState.RELEASED;
        }

    }

    private void parseButtonStates(byte channelA) {
        switch (channelA) {
        case 0:
            buttonAI = ButtonState.PRESSED;
            break;
        case 1:
            buttonAO = ButtonState.PRESSED;
            break;
        case 2:
            buttonBI = ButtonState.PRESSED;
            break;
        case 3:
            buttonBO = ButtonState.PRESSED;
            break;

        default:
            break;
        }

    }

    @Override
    public String toString() {
        return "buttonAI=" + buttonAI + ", buttonAO=" + buttonAO + ", buttonBI=" + buttonBI + ", buttonBO=" + buttonBO;
    }

    public enum NUState {
        UNASSIGNEDMESSAGE(0), NORMALMESSAGE(1);

        private final int enumvalue;

        NUState(int value) {
            this.enumvalue = value;
        }

        NUState(byte value) {
            this.enumvalue = value;
        }

        public byte toByte() {
            return (byte) enumvalue;
        }

        @Override
        public String toString() {
            return (enumvalue == 0) ? "Unassigned" : "Normal";
        }
    }

    public enum T21State {
        PTMType1(0), PTMType2(1);

        private final int enumvalue;

        T21State(int value) {
            this.enumvalue = value;
        }

        T21State(byte value) {
            this.enumvalue = value;
        }

        public byte toByte() {
            return (byte) enumvalue;
        }

        @Override
        public String toString() {
            return (enumvalue == 0) ? "PTM Type 1" : "PTM Type 2";
        }
    }

}
