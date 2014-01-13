package at.ac.tuwien.auto.iotsys.gateway.connectors.enocean.util;

public class SerialTest {

	public static void main(String[] args)
	{
		final EnoceanSerialCom comm = new EnoceanSerialCom("/dev/ttyUSB0");
        try
        {
        	comm.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                comm.closePort();
            }
        });
	}
}
