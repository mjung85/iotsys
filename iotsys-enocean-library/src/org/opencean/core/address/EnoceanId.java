package org.opencean.core.address;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.opencean.core.utils.ByteArray;

public class EnoceanId {

    private byte[] id;

    public static EnoceanId fromByteArray(byte[] array, int startPos) {
        byte[] id = new byte[4];
        System.arraycopy(array, startPos, id, 0, 4);
        return new EnoceanId(id);
    }

    public static EnoceanId fromInt(int idInt) {
        return new EnoceanId(new ByteArray().addInt(idInt).getArray());
    }

    public static EnoceanId fromString(String idString) {
        String[] numbers = idString.trim().split("[:]");
        return new EnoceanId(new byte[] { parseByte(numbers[0]), parseByte(numbers[1]), parseByte(numbers[2]), parseByte(numbers[3]) });
    }

    private static byte parseByte(String number) {
        return (byte) Integer.parseInt(number, 16);
    }

    public EnoceanId(byte[] id) {
        this.id = id;
    }

    public byte[] toBytes() {
        return id;
    }
    
    public int toInt() {
    	ByteBuffer bb = ByteBuffer.wrap(id);
        return bb.getInt();
    }

    @Override
    public String toString() {
        return String.format("%1$02X:%2$02X:%3$02X:%4$02X", id[0], id[1], id[2], id[3]);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(id);
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
        EnoceanId other = (EnoceanId) obj;
        if (!Arrays.equals(id, other.id)) {
            return false;
        }
        return true;
    }

}
