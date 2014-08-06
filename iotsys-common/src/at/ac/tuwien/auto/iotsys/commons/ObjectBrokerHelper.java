package at.ac.tuwien.auto.iotsys.commons;


public class ObjectBrokerHelper{
	
	private static ObjectBroker instance;

	public static ObjectBroker getInstance() {
		return instance;
	}

	public static void setInstance(ObjectBroker instance) {
		ObjectBrokerHelper.instance = instance;
	}

}
