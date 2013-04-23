package at.ac.tuwien.auto.iotsys.gateway.interceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.auto.iotsys.commons.interceptor.ClassAlreadyRegisteredException;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Interceptor;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorBroker;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorRequest;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse;
import at.ac.tuwien.auto.iotsys.commons.interceptor.Outcome;
import at.ac.tuwien.auto.iotsys.commons.interceptor.InterceptorResponse.StatusCode;

public class InterceptorBrokerImpl implements InterceptorBroker, Interceptor {

	private static InterceptorBroker instance = new InterceptorBrokerImpl(); 
	
	private Map<Class, Interceptor> interceptors = new HashMap<Class, Interceptor>();
	
	private InterceptorBrokerImpl() {
		
	}
	
	public static InterceptorBroker getInstance() {
		return instance;
	}

	@Override
	public Map<Class, Interceptor> getInterceptors() {
		return interceptors;
	}

	@Override
	public boolean hasInterceptors() {
		if (interceptors.size() > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public InterceptorResponse handleRequest(InterceptorRequest request) {
		InterceptorResponse resp = null;
		for (Interceptor i : interceptors.values()) {
			resp = i.handleRequest(request);
			if (!resp.getStatus().equals(StatusCode.OK)) {				
				return resp;
			}
		}
		if (resp != null) {
			return resp;
		}
		return null;
	}

	@Override
	public InterceptorResponse handleResponse(InterceptorResponse response) {
		InterceptorResponse resp = null;
		for (Interceptor i : interceptors.values()) {
			resp = i.handleResponse(response);
			if (!resp.getStatus().equals(StatusCode.OK)) {				
				return resp;
			}
		}
		if (resp != null) {
			return resp;
		}
		return null;
	}

	@Override
	public void register(Interceptor i) throws ClassAlreadyRegisteredException {
		if (interceptors.containsKey(i.getClass())) {
			throw new ClassAlreadyRegisteredException();
		}
		interceptors.put(i.getClass(), i);		
	}

	@Override
	public void unregister(Interceptor i) {
		if (interceptors.containsKey(i.getClass())) {
			interceptors.remove(i.getClass());
		}
	}
}
