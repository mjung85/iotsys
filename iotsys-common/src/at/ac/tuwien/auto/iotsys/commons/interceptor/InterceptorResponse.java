package at.ac.tuwien.auto.iotsys.commons.interceptor;

public interface InterceptorResponse {
	public enum StatusCode {
		OK,
		ERROR,
		PERMISSION_DENIED
	}
	
	public boolean forward();
	
	public StatusCode getStatus();
	
	public String getMessage();
	
	
}
