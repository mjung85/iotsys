package at.ac.tuwien.auto.iotsys.commons.interceptor;

public interface Interceptor {
	
	public InterceptorResponse handleRequest(InterceptorRequest request);
	
	public InterceptorResponse handleResponse(InterceptorResponse response);
}
