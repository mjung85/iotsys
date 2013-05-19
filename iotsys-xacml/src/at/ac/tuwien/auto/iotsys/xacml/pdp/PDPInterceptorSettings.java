package at.ac.tuwien.auto.iotsys.xacml.pdp;

public class PDPInterceptorSettings {
	private static final PDPInterceptorSettings instance = new PDPInterceptorSettings();
	
	private String policyFile = "xacml-policy.xml";
	// private String policyFile = "xacml-policy-no-smartmeter.xml";
	
	private PDPInterceptorSettings() {
		
	}

	public static final PDPInterceptorSettings getInstance() {
		return instance;
	}
	
	public synchronized String getPolicyFile() {
		return policyFile;
	}

	public synchronized void setPolicyFile(String policyFile) {
		this.policyFile = policyFile;
	}
}
