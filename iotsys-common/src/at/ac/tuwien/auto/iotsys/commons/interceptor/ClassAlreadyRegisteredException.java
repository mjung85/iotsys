package at.ac.tuwien.auto.iotsys.commons.interceptor;

public class ClassAlreadyRegisteredException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4609493217190353461L;
	private static String msg = "The class you are going to add as interceptor is already registered.";
	
	public ClassAlreadyRegisteredException() {
		super(msg);
	}
	
	public ClassAlreadyRegisteredException(Throwable t) {
		super(msg, t);
	}
}
