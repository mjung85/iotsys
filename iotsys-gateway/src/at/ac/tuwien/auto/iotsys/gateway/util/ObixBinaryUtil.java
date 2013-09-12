package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import obix.Obj;
import obix.io.BinObixDecoder;
import obix.io.BinObixEncoder;
import obix.io.ObixDecoder;
import obix.io.ObixEncoder;

public class ObixBinaryUtil {
	public static void main(String[] args) {
		encode("in.xml", "out.obix");
		decode("out.obix", "out.xml");
	}

	public static void encode(String inFile, String outFile) {
		byte[] binObject = BinObixEncoder.toBytes(ObixDecoder
				.fromString(readFileAsString(inFile)));
		writeByteArrayToFile(outFile, binObject);
	}

	public static void decode(String inFile, String outFile) {
		Obj obj = BinObixDecoder.fromBytes(readFileAsBytes(inFile));
		String obix = ObixEncoder.toString(obj);
		writeStringToFile(outFile, obix);	
	}

	private static void writeStringToFile(String outFile, String text) {
		try {
			PrintWriter out = new PrintWriter(outFile);
			out.write(text);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String readFileAsString(String filename) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private static void writeByteArrayToFile(String filename, byte[] bytes) {
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(filename));
			fos.write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static byte[] readFileAsBytes(String filename) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		InputStream in = null;
		try {
			in = new FileInputStream(filename);
			byte[] buf = new byte[1024];
		
			while (true) {
				int r = in.read(buf);
				if (r == -1) {
					break;
				}
				out.write(buf, 0, r);
			}
		
		} catch (IOException e) {

			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out.toByteArray();
	}
}
