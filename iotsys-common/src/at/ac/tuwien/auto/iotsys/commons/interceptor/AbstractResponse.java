package at.ac.tuwien.auto.iotsys.commons.interceptor;

public abstract class AbstractResponse implements InterceptorResponse {

	protected boolean forward = false;
	
	protected StatusCode status;
	
	public AbstractResponse() {
		
	}
	
	public AbstractResponse(StatusCode status, boolean forward) {
		this.status = status;
		this.forward = forward;
	}
	
	@Override
	public boolean forward() {
		return forward;
	}

	@Override
	public StatusCode getStatus() {
		return status;
	}

	@Override
	public abstract String getMessage();
}
