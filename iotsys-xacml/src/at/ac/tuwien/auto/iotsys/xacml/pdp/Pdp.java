package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.util.Map;

import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;

public interface Pdp {

	public boolean evaluate(String resource, String subject, String action,
			Map<Parameter, String> params);
	
}
