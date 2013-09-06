package at.ac.tuwien.auto.iotsys.commons.interceptor;

import java.util.Map;

public interface InterceptorBroker extends Interceptor {
	public Map<Class, Interceptor> getInterceptors();
	public boolean hasInterceptors();
	public void register(Interceptor i) throws ClassAlreadyRegisteredException;
	public void unregister(Interceptor i);
}
