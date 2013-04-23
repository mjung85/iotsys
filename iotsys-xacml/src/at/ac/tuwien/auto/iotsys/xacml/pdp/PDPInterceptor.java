package at.ac.tuwien.auto.iotsys.xacml.pdp;

import java.util.logging.Logger;

import at.ac.tuwien.auto.iotsys.commons.interceptor.AbstractResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorRequest;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse.StatusCode;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Parameter;

public class PDPInterceptor implements Interceptor {

	private Logger log = Logger.getLogger(PDPInterceptor.class.getName());
	
	@Override
	public InterceptorResponse handleRequest(InterceptorRequest request) {
		log.info("Incoming request to PDPInteceptor");
		
		EnterprisePDP pdp = new EnterprisePDP();
		String resource = request.getInterceptorParam(Parameter.RESOURCE);
		String subject = request.getInterceptorParam(Parameter.SUBJECT);
		String method = request.getInterceptorParam(Parameter.ACTION);

		if (!pdp.evaluate(resource, subject, method,
				request.getInterceptorParams())) {
			return new Response(StatusCode.PERMISSION_DENIED, true);
		}
		return new Response(StatusCode.OK, false);
	}

	@Override
	public InterceptorResponse handleResponse(InterceptorResponse response) {
		// TODO Auto-generated method stub
		return null;
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
