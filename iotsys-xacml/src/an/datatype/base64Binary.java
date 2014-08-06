package an.datatype;

import java.io.IOException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class base64Binary {
    private String base64Value;
    private byte[] value;
    private static BASE64Decoder decoder = new BASE64Decoder();
    private static BASE64Encoder encoder = new BASE64Encoder();
    private int hashCode;

    public base64Binary(String base64Value) throws IOException {
        this.base64Value = base64Value;
        this.value = decoder.decodeBuffer(base64Value);
        this.hashCode = base64Value.hashCode();
    }

    public base64Binary(byte[] value) {
        this.value = value;
        this.base64Value = encoder.encode(value);
        this.hashCode = base64Value.hashCode();
    }

    public static base64Binary valueOf(String bsae64Value) throws IOException {
        return new base64Binary(bsae64Value);
    }

    public byte[] getValue() {
        return value;
    }

    public String getBase64Value() {
        return base64Value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            byte[] otherValue = ((base64Binary)o).value;
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
        return getBase64Value();
    }

    public int hashCode() {
        return hashCode;
    }
}