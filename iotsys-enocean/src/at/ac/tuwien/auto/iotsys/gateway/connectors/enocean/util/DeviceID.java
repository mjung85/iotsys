/*******************************************************************************
 * Copyright (c) 2013
 * Institute of Computer Aided Automation, Automation Systems Group, TU Wien.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the IoTSyS project.
 ******************************************************************************/

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
