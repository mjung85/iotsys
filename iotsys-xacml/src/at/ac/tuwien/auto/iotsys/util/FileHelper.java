package at.ac.tuwien.auto.iotsys.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileHelper {
	
	public static String readFile(String path) throws IOException {
		// System.out.println(System.getProperty("java.class.path"));
		
		InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path);
		if (in == null) {
			in = new FileInputStream(path);
		}
		System.out.println(path);
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer builder = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				builder.append(line).append("\n");
			}
			br.close();

			return builder.toString();
			// return Charset.defaultCharset().decode().toString();
		} finally {
			in.close();
		}
	}
}