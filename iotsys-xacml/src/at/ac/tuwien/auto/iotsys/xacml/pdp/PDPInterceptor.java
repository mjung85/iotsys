package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.interceptor.AbstractResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorRequest;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse.StatusCode;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;

/**
 * 
 * @author Thomas Hofer
 *
 */
public class PDPInterceptor implements Interceptor {

	private Logger log = Logger.getLogger(PDPInterceptor.class.getName());
	
	private String resourcePrefix = "res";
	
	private final Response OK_RESPONSE = new Response(StatusCode.OK, false);
	
	private final EnterprisePDP pdp = new EnterprisePDP(resourcePrefix);
	
	public PDPInterceptor() {
		
	}
	
	public PDPInterceptor(String resourcePrefix) {
		this.resourcePrefix = resourcePrefix;
	}
	
	@Override
	public synchronized InterceptorResponse handleRequest(InterceptorRequest request) {
		log.info("Incoming request to PDPInteceptor");
		
		if(!PDPInterceptorSettings.getInstance().active()){
			log.info("Returning ok_response");
			return OK_RESPONSE;
		}
		
		
		String resource = request.getInterceptorParam(Parameter.RESOURCE);
		String subject = request.getInterceptorParam(Parameter.SUBJECT);
		String method = request.getInterceptorParam(Parameter.ACTION);
		log.info("evaluate request");
		if (!pdp.evaluate(resource, subject, method,
				request.getInterceptorParams())) {
			log.info("permission denied");
			return new Response(StatusCode.PERMISSION_DENIED, true);
		}
		log.info("permission ok");
		return OK_RESPONSE;
	}

	@Override
	public InterceptorResponse handleResponse(InterceptorResponse response) {
		throw new UnsupportedOperationException();
	}

	private class Response extends AbstractResponse implements InterceptorResponse {

		public Response(StatusCode status, boolean forward) {
			this.status = status;
			this.forward = forward;
		}
		
		@Override
		public String getMessage() {
			return "Access denied for the requested resource.";
		}
	}

}
