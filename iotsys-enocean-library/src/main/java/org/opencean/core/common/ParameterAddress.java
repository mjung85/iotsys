package org.opencean.core.common;

/**
 * A parameter is part of a physical device that holds a specific value. It can
 * be r/w or r/o.
 * 
 * @author Thomas Letsch (contact@thomas-letsch.de)
 * 
 */
public interface ParameterAddress {

    String getParameterId();

    String getDeviceId();

    String getDeviceAsString();

    String getChannelId();

    String getChannelAsString();

    String getAsString();
}
