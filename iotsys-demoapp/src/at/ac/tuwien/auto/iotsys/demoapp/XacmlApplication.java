package at.ac.tuwien.auto.iotsys.demoapp;

import obix.Bool;
import obix.Obj;
import obix.Uri;
import at.ac.tuwien.auto.iotsys.commons.obix.objects.iot.logic.Application;
import at.ac.tuwien.auto.iotsys.xacml.pdp.PDPInterceptorSettings;

public class XacmlApplication extends Obj implements Application{
	protected Bool enabled = new Bool(false);
	
	public XacmlApplication(){
		this.setName("PrivacyGuard");
		enabled.setName("enabled");
		enabled.setHref(new Uri("enabled"));
		enabled.setWritable(true);
		
		this.add(enabled);
	}

	@Override
	public Bool enabled() {
		return enabled;
	}
	
	@Override
	public void writeObject(Obj input){
		
		if(input instanceof Bool){
			this.enabled.set( ((Bool) input).get());
		}
		
		if(this.enabled.get()){
			PDPInterceptorSettings.getInstance().setActive(true);
			PDPInterceptorSettings.getInstance().setPolicyFile("xacml-policy-no-smartmeter.xml");	
		}
		else{
			PDPInterceptorSettings.getInstance().setActive(false);
			PDPInterceptorSettings.getInstance().setPolicyFile("xacml-policy.xml");		
		}
	}
}
