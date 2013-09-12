package at.ac.tuwien.auto.iotsys.commons.interceptor;

import java.util.Map;

public interface InterceptorRequest {
	public Map<Parameter, String> getInterceptorParams();
	
	public void setInterceptorParams(Map<Parameter, String> params);
	
	public String getInterceptorParam(Parameter p);
	
	public void setInterceptorParam(Parameter p, String s);
	
	public Map<String, String> getHeaders();
	
	public void setHeaders(Map<String, String> headers);
	
	public String getHeader(String header);
	
	public void setHeader(String header, String value);
	
	public Map<String, String> getRequestParams();
	
	public void setRequestParams(Map<String, String> params);
	
	public String getRequestParam(String key);
	
	public void setRequestParam(String key, String value);
}
