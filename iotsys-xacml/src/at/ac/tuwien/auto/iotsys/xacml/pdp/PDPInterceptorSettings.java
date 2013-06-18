package at.ac.tuwien.auto.iotsys.xacml.pdp;

public class PDPInterceptorSettings {
	private static final PDPInterceptorSettings instance = new PDPInterceptorSettings();
	
	private volatile boolean active = false;
	
	private String policyFile = "xacml-policy.xml";
	// private String policyFile = "xacml-policy-no-smartmeter.xml";
	
	private String remotePdpWsdl = "http://localhost:8080/SwgPdp?wsdl";
	
	private boolean remotePdp = false;
	
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
	
	public synchronized boolean active(){
		return active;
	}
	
	public synchronized void setActive(boolean active){
		this.active = active;
	}

	public String getRemotePdpWsdl() {
		return remotePdpWsdl;
	}

	public void setRemotePdpWsdl(String remotePdpWsdl) {
		this.remotePdpWsdl = remotePdpWsdl;
	}

	public boolean isRemotePdp() {
		return remotePdp;
	}

	public void setRemotePdp(boolean remotePdp) {
		this.remotePdp = remotePdp;
	}
}
