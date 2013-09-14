package javax.jmdns.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.mdnssd.NamedImpl;

/**
 * @author Mr.Nam
 * 
 */
public class MdnsUtils {
	public static final int IPv4 = 1;
	public static final int IPv6 = 2;

	public static InetAddress getByAddress(String addr) {
		if (addr.contains("/")){
			String[] arr = addr.split("/");
			addr = arr[arr.length - 1];
		}
			
		try {

			byte[] bytes;
			bytes = toByteArray(addr, IPv4);
			if (bytes != null) {

				return InetAddress.getByAddress(addr, bytes);

			}
			bytes = toByteArray(addr, IPv6);
			if (bytes != null) {
				return InetAddress.getByAddress(addr, bytes);
			}
			throw new UnknownHostException("Invalid address: " + addr);

		} catch (UnknownHostException ex) {
			Logger.getLogger(NamedImpl.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	public static byte[] toByteArray(String s, int family) {
		if (family == IPv4) {
			return parseV4(s);
		} else if (family == IPv6) {
			return parseV6(s);
		} else {
			throw new IllegalArgumentException("unknown address family");
		}
	}

	public static byte[] parseV4(String s) {
		int numDigits;
		int currentOctet;
		byte[] values = new byte[4];
		int currentValue;
		int length = s.length();

		currentOctet = 0;
		currentValue = 0;
		numDigits = 0;
		for (int i = 0; i < length; i++) {
			char c = s.charAt(i);
			if (c >= '0' && c <= '9') {
				/*
				 * Can't have more than 3 digits per octet.
				 */
				if (numDigits == 3) {
					return null;
				}
				/*
				 * Octets shouldn't start with 0, unless they are 0.
				 */
				if (numDigits > 0 && currentValue == 0) {
					return null;
				}
				numDigits++;
				currentValue *= 10;
				currentValue += (c - '0');
				/*
				 * 255 is the maximum value for an octet.
				 */
				if (currentValue > 255) {
					return null;
				}
			} else if (c == '.') {
				/*
				 * Can't have more than 3 dots.
				 */
				if (currentOctet == 3) {
					return null;
				}
				/*
				 * Two consecutive dots are bad.
				 */
				if (numDigits == 0) {
					return null;
				}
				values[currentOctet++] = (byte) currentValue;
				currentValue = 0;
				numDigits = 0;
			} else {
				return null;
			}
		}
		/*
		 * Must have 4 octets.
		 */
		if (currentOctet != 3) {
			return null;
		}
		/*
		 * The fourth octet can't be empty.
		 */
		if (numDigits == 0) {
			return null;
		}
		values[currentOctet] = (byte) currentValue;
		return values;
	}

	public static byte[] parseV6(String s) {
		int range = -1;
		byte[] data = new byte[16];

		String[] tokens = s.split(":", -1);

		int first = 0;
		int last = tokens.length - 1;

		if (tokens[0].length() == 0) {
			// If the first two tokens are empty, it means the string
			// started with ::, which is fine. If only the first is
			// empty, the string started with :, which is bad.
			if (last - first > 0 && tokens[1].length() == 0) {
				first++;
			} else {
				return null;
			}
		}

		if (tokens[last].length() == 0) {
			// If the last two tokens are empty, it means the string
			// ended with ::, which is fine. If only the last is
			// empty, the string ended with :, which is bad.
			if (last - first > 0 && tokens[last - 1].length() == 0) {
				last--;
			} else {
				return null;
			}
		}

		if (last - first + 1 > 8) {
			return null;
		}

		int i, j;
		for (i = first, j = 0; i <= last; i++) {
			if (tokens[i].length() == 0) {
				if (range >= 0) {
					return null;
				}
				range = j;
				continue;
			}

			if (tokens[i].indexOf('.') >= 0) {
				// An IPv4 address must be the last component
				if (i < last) {
					return null;
				}
				// There can't have been more than 6 components.
				if (i > 6) {
					return null;
				}
				byte[] v4addr = toByteArray(tokens[i], IPv4);
				if (v4addr == null) {
					return null;
				}
				for (int k = 0; k < 4; k++) {
					data[j++] = v4addr[k];
				}
				break;
			}

			try {
				for (int k = 0; k < tokens[i].length(); k++) {
					char c = tokens[i].charAt(k);
					if (Character.digit(c, 16) < 0) {
						return null;
					}
				}
				int x = Integer.parseInt(tokens[i], 16);
				if (x > 0xFFFF || x < 0) {
					return null;
				}
				data[j++] = (byte) (x >>> 8);
				data[j++] = (byte) (x & 0xFF);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		if (j < 16 && range < 0) {
			return null;
		}

		if (range >= 0) {
			int empty = 16 - j;
			System.arraycopy(data, range, data, range + empty, j - range);
			for (i = range; i < range + empty; i++) {
				data[i] = 0;
			}
		}

		return data;
	}

}
