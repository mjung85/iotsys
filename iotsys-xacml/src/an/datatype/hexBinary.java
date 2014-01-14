package an.datatype;

public class hexBinary {
    private String strValue;
    private byte[] value;
    private int hashCode;

    public hexBinary(String strValue) throws InvalidHexBinaryException {
        this.strValue = strValue;
        if (strValue.length() % 2 != 0) {
            throw new InvalidHexBinaryException("'" + strValue + "' is not a valid hex value.");
        }
        value = new byte[strValue.length() / 2];
        for (int i = 0; i < value.length; i ++) {
            value[i] = (byte)Integer.parseInt(strValue.substring(i * 2, (i + 1) * 2), 16);
        }
        this.hashCode = strValue.hashCode();
    }

    public hexBinary(byte[] value) {
        this.value = value;
        StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < value.length; i ++) {
            strBuf.append(Integer.toHexString(value[i]));
        }
        strValue = strBuf.toString().toUpperCase();
        this.hashCode = strValue.hashCode();
    }

    public static hexBinary valueOf(String strValue) throws InvalidHexBinaryException {
        return new hexBinary(strValue);
    }

    public byte[] getValue() {
        return value;
    }

    public String getStringValue() {
        return strValue;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            byte[] otherValue = ((hexBinary)o).value;
            if (value.length != otherValue.length) {
                return false;
            }
            for (int i = 0; i < value.length; i ++) {
                if (value[i] != otherValue[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public String toString() {
        return getStringValue();
    }

    public int hashCode() {
        return hashCode;
    }
}