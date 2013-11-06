package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

/*
 * Example Packets:
 * Switch On (Press):   55 00 07 07 01 7a f6 50 00 10 20 2b 30 01 ff ff ff ff 36 00 92 
 * Switch On (Release): 55 00 07 07 01 7a f6 00 00 10 20 2b 20 01 ff ff ff ff 36 00 21
 * */

public final class CRC8Test {

    public static byte[] hexStringToByteArray(final String s)
    {
    	final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
        	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
	public static void main(final String[] args)
	{
		byte crc8;
		
		// test over header
		final String headerString = "00070701"; // crc value should be 0x7a
		crc8 = CRC8Hash.calculate(hexStringToByteArray(headerString));
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		
		// test over data + opt data
		final String dataString = "f6500010202b3001ffffffff3600"; // crc value should be 0x92
		crc8 = CRC8Hash.calculate(hexStringToByteArray(dataString));
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		
		// test over range in whole frame
		final String wholeFrameString = "55000707017af6000010202b2001ffffffff360021";
		crc8 = CRC8Hash.calculate(hexStringToByteArray(wholeFrameString), 1, 4); // for header
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
		crc8 = CRC8Hash.calculate(hexStringToByteArray(wholeFrameString), 6, 14); // for data + opt data
		System.out.println("CRC8: 0x" + Integer.toHexString(crc8 & 0xFF));
	}
}
