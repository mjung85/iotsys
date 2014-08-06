package org.opencean.core.address;

import org.opencean.core.common.Parameter;
import org.opencean.core.common.ParameterAddress;

public class EnoceanParameterAddress implements ParameterAddress {

    private EnoceanId deviceId;
    private String channelId;
    private String parameterId;

    public EnoceanParameterAddress(EnoceanId enoceanId) {
        this.deviceId = enoceanId;
    }

    public EnoceanParameterAddress(EnoceanId enoceanId, String parameterId) {
        this(enoceanId);
        this.parameterId = parameterId;
    }

    public EnoceanParameterAddress(EnoceanId enoceanId, Parameter parameter) {
        this(enoceanId, parameter.name());
    }

    public EnoceanParameterAddress(EnoceanId enoceanId, String channelId, Parameter parameter) {
        this(enoceanId, channelId, parameter.name());
    }

    public EnoceanParameterAddress(EnoceanId enoceanId, String channelId, String parameterId) {
        this(enoceanId, parameterId);
        this.channelId = channelId;
    }

    @Override
    public String getDeviceAsString() {
        return "{" + "id=\"" + deviceId + "\"}";
    }

    @Override
    public String getChannelAsString() {
        return "{" + "id=\"" + deviceId + "\"" + ", channel=\"" + channelId + "\"}";
    }

    @Override
    public String getAsString() {
        String str = "{" + "id=\"" + deviceId + "\"";
        if (channelId != null) {
            str += ", channel=\"" + channelId + "\"";
        }
        if (parameterId != null) {
            str += ", parameter=\"" + parameterId + "\"";
        }
        str += "}";
        return str;
    }

    @Override
    public String getParameterId() {
        return parameterId;
    }

    @Override
    public String getChannelId() {
        return channelId;
    }

    @Override
    public String getDeviceId() {
        return deviceId.toString();
    }

    public EnoceanId getEnoceanDeviceId() {
        return deviceId;
    }

    @Override
    public String toString() {
        return "EnoceanParameter: " + getAsString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channelId == null) ? 0 : channelId.hashCode());
        result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
        result = prime * result + ((parameterId == null) ? 0 : parameterId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        EnoceanParameterAddress other = (EnoceanParameterAddress) obj;
        if (channelId == null) {
            if (other.channelId != null) {
                return false;
            }
        } else if (!channelId.equals(other.channelId)) {
            return false;
        }
        if (deviceId == null) {
            if (other.deviceId != null) {
                return false;
            }
        } else if (!deviceId.equals(other.deviceId)) {
            return false;
        }
        if (parameterId == null) {
            if (other.parameterId != null) {
                return false;
            }
        } else if (!parameterId.equals(other.parameterId)) {
            return false;
        }
        return true;
    }

}
