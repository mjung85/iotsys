package org.opencean.core;

import org.opencean.core.packets.BasicPacket;

public interface EnoceanReceiver {

    void receivePacket(BasicPacket packet);

}
