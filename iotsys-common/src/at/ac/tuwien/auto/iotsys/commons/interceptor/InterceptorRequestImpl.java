package at.ac.tuwien.auto.iotsys.commons.interceptor;

import java.util.HashMap;
import java.util.Map;

public class InterceptorRequestImpl implements InterceptorRequest {

	private Map<Parameter, String> interceptorParams = new HashMap<Parameter, String>();
	
	private Map<String, String> headerParams = new HashMap<String, String>();
	
	private Map<String, String> requestParams = new HashMap<String, String>();
	
	@Override
	public Map<Parameter, String> getInterceptorParams() {
		return interceptorParams;
	}

	@Override
	public void setInterceptorParams(Map<Parameter, String> params) {
		this.interceptorParams = params;
	}

	@Override
	public String getInterceptorParam(Parameter p) {
		return interceptorParams.get(p);
	}

	@Override
	public void setInterceptorParam(Parameter p, String s) {
		interceptorParams.put(p, s);
	}	
	
	@Override
	public Map<String, String> getHeaders() {
		return headerParams;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headerParams = headers;
	}

	@Override
	public String getHeader(String header) {
		return headerParams.get(header);
	}

	@Override
	public void setHeader(String header, String value) {
		headerParams.put(header, value);
	}
	
	@Override
	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	@Override
	public void setRequestParams(Map<String, String> params) {
		this.requestParams = params;
	}
	
	@Override
	public String getRequestParam(String key) {
		return requestParams.get(key);
	}

	@Override
	public void setRequestParam(String key, String value) {
		requestParams.put(key, value);
	}
}
