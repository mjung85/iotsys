package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;


public final class CRC8Hash
{
	private static final byte[] CRC8_TABLE = calculateCRC8Table();
	
	private CRC8Hash () {}
	
	private static byte[] calculateCRC8Table()
	{
		byte[] table = new byte[256];
		int generatorPoly = 0x07;
		int result;
		
		for (int i = 1; i < table.length; i++)
		{
			result = i;
			
			for (int j = 0; j < 8; j++)
			{
				if ((result & 0x80) != 0)
				{
					result = (result << 1) ^ generatorPoly;
				}
				else
				{
					result = result << 1;
				}
			}
			table[i] = (byte)result; 
		}
		return table;
	}
	
	public static byte calculate(byte[] data)
	{
		return calculate(data, 0, data.length);
	}
	
	public static byte calculate(byte[] data, int offset, int len)
	{
		byte crc = 0;
		
		for (int i = offset; i < (offset + len); i++)
		{
			crc = CRC8_TABLE[(crc ^ data[i]) & 0xFF];
		}
		return crc;
	}
}
