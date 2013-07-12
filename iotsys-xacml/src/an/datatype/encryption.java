package an.datatype;

import java.io.IOException;

public class encryption extends base64Binary {
    public encryption(String base64Encrypted) throws IOException {
        super(base64Encrypted);
    }

    public static encryption valueOf(String base64Encrypted) throws IOException {
        return new encryption(base64Encrypted);
    }
}