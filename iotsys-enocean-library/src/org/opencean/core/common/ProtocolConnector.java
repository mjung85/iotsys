package org.opencean.core.common;

public interface ProtocolConnector {

    void connect(String device);

    void disconnect();

    byte get();

    short getShort();

    void get(byte[] data);

    void mark();

    void reset();

    void write(byte[] data);

}