package org.opencean.core;

import java.util.ArrayList;
import java.util.List;

import org.opencean.core.address.EnoceanId;
import org.opencean.core.common.EEPId;
import org.opencean.core.common.ParameterValueChangeListener;
import org.opencean.core.common.ProtocolConnector;
import org.opencean.core.packets.BasicPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESP3Host extends Thread {
    private static Logger logger = LoggerFactory.getLogger(ESP3Host.class);

    private List<EnoceanReceiver> receivers = new ArrayList<EnoceanReceiver>();

    final ProtocolConnector connector;

    private ParameterChangeNotifier parameterChangeNotifier;

    public ESP3Host(ProtocolConnector connector) {
        this.connector = connector;
        parameterChangeNotifier = new ParameterChangeNotifier();
        parameterChangeNotifier.addParameterValueChangeListener(new LoggingListener());
        receivers.add(parameterChangeNotifier);
    }

    public void addDeviceProfile(EnoceanId id, EEPId epp) {
        parameterChangeNotifier.addDeviceProfile(id, epp);
    }

    public void addParameterChangeListener(ParameterValueChangeListener listener) {
        parameterChangeNotifier.addParameterValueChangeListener(listener);
    }

    public void addListener(EnoceanReceiver receiver) {
        this.receivers.add(receiver);
    }

    public void removeListener(EnoceanReceiver receiver) {
        receivers.remove(receiver);
    }

    public void sendRadio(BasicPacket packet) {
        connector.write(packet.toBytes());
    }

    private void notifyReceivers(BasicPacket receivedPacket) {
        for (EnoceanReceiver receiver : this.receivers) {
            receiver.receivePacket(receivedPacket);
        }
    }

    public void sendRadioSubTel() {

    }

    public void receiveRadioSubTel() {

    }

    @Override
    public void run() {
        logger.info("starting receiveRadio.. ");
        PacketStreamReader receiver = new PacketStreamReader(connector);
        while (true) {
            try {
                BasicPacket receivedPacket = receiver.read();
                if (receivedPacket != null) {
                    logger.info(receivedPacket.toString());
                    notifyReceivers(receivedPacket);
                } else {
                    logger.debug("Sync byte received, but header not valid.");
                }
            } catch (Exception e) {
                logger.error("Error", e);
            }
        }
    }

}
