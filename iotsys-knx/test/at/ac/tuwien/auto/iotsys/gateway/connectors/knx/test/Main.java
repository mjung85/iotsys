package at.ac.tuwien.auto.iotsys.gateway.connectors.knx.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;

import at.ac.tuwien.auto.calimero.GroupAddress;
import at.ac.tuwien.auto.calimero.dptxlator.DPTXlator3BitControlled;
import at.ac.tuwien.auto.calimero.exception.KNXException;
import at.ac.tuwien.auto.calimero.exception.KNXFormatException;
import at.ac.tuwien.auto.calimero.process.ProcessCommunicator;
import at.ac.tuwien.auto.iotsys.commons.Connector;
import at.ac.tuwien.auto.iotsys.gateway.connectors.knx.KNXConnector;

public class Main {
	private static Logger log = Logger.getLogger(Main.class.getName());

	private XMLConfiguration devicesConfig;

	private ArrayList<String> myObjects = new ArrayList<String>();
	
	public static void main(String[] args){
		(new Main()).test();
	}
	
	public void test(){
		ArrayList<Connector> connectors = new ArrayList<Connector>();

		KNXConnector knxConnector = new KNXConnector("192.168.161.59", 3671, "auto");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		
		try
		{
			knxConnector.connect();
			
			try {
				while(!br.readLine().equals("q"))
					knxConnector.getProcessCommunicator().write(new GroupAddress("1/0/1"), ProcessCommunicator.BOOL_INCREASE,(byte) 4);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			try {
				br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				while(!br.readLine().equals("q"))
					knxConnector.getProcessCommunicator().write(new GroupAddress("1/0/1"), ProcessCommunicator.BOOL_DECREASE,(byte) 4);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
//			byte readControl = knxConnector.getProcessCommunicator().readControl(new GroupAddress("1/0/1"));
			
			DPTXlator3BitControlled x = new DPTXlator3BitControlled(DPTXlator3BitControlled.DPT_CONTROL_DIMMING);

//			x.setData(new byte[]{readControl}, 0);
//			System.out.println("Step code: " + x.getStepCode());
//			System.out.println("Control bit: " + x.getControlBit());
//			System.out.println("Intervals: " + x.getIntervals());
			
			
			
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (KNXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			br.readLine();
			knxConnector.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
