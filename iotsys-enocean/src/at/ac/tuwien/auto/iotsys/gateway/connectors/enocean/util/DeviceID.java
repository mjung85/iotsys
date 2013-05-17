package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class DeviceID {
    private final long deviceID;

    static final long MIN_ID = 0;
    static final long MAX_ID = 0xFFFFFFFFL;
    static final long MIN_BASE_ID = 0xFF800000L;
    static final long MAX_BASE_ID = 0xFFFFFF80L;
    static final long MIN_BASE_ID_OFFSET = 0;
    static final long MAX_BASE_ID_OFFSET = 127;
    public static final int ID_LENGTH = 4;

    public static final DeviceID BROADCAST_ID = new DeviceID(0xFFFFFFFFL);

    private DeviceID(long deviceIDAs32BitValue)
    {
        this.deviceID = deviceIDAs32BitValue & 0xFFFFFFFFL;
    }

    public static DeviceID fromByteArray (byte[] deviceID)
    {
        if (deviceID == null)
            return null;

        if (deviceID.length != ID_LENGTH)
            return null;

        long deviceIDValue = 0;
        deviceIDValue += (((long)deviceID[0] & 0xFFL) << 24);
        deviceIDValue += (((long)deviceID[1] & 0xFFL) << 16);
        deviceIDValue += (((long)deviceID[2] & 0xFFL) << 8);
        deviceIDValue +=  ((long)deviceID[3] & 0xFFL);

        return new DeviceID(deviceIDValue);
    }

    public static DeviceID fromString (String deviceID)
    {
        return new DeviceID(parseID(deviceID));
    }

    public byte[] toByteArray ()
    {
        byte[] deviceIDByteArray = new byte[4];

        deviceIDByteArray[0] = (byte)((deviceID >> 24) & 0xFF);
        deviceIDByteArray[1] = (byte)((deviceID >> 16) & 0xFF);
        deviceIDByteArray[2] = (byte)((deviceID >> 8) & 0xFF);
        deviceIDByteArray[3] = (byte)(deviceID & 0xFF);

        return deviceIDByteArray;
    }

    public String toString()
    {
        return String.format("0x%08X", deviceID);
    }

    private static long parseID (String id)
    {
        return Long.decode(id);
    }

    public long getDeviceID()
    {
        return this.deviceID;
    }
}
