package at.ac.tuwien.auto.iotsys.gateway.obix.observer;

import java.util.LinkedList;
import java.util.List;

import obix.Obj;

/**
 * Observes the state changes of an obix object. Holds a history 
 * of the changes until the events are polled or the max number of 
 * elements is exceeded.
 * 
 * This class also acts as a singleton subject, in order to allow CoAP updates on observed resources.
 *
 */
public class ObjObserver<ObjType extends Obj> implements EventObserver<ObjType> {
	private Subject subject;
	private static ExternalObserver observer = null;
	
	public static final int MAX_EVENTS = 20;
	private static final Object lock = new Object();
	private LinkedList<ObjType> queue = new LinkedList<ObjType>();
	
	public ObjObserver() {
	}
		
	@Override
	public void update(Object currentState) {
		synchronized(lock) {
			queue.add((ObjType) currentState);
			if(queue.size() > MAX_EVENTS){
				queue.removeLast();
			}			
		}
		
		// notify any CoAP observers
		Obj obj = (Obj) subject;
		if(observer != null)
			observer.objectChanged(obj.getFullContextPath());
	}
	
	/**
	 * Provides the latest events.
	 * @return 
	 */
	public List<ObjType> pollChanges() {
		LinkedList<ObjType> ret = null;
		synchronized(lock){
			ret = queue;
			queue = new LinkedList<ObjType>();
		}
		return ret;
	}
	
	/**
	 * Indicates if changes are available.
	 */
	public boolean objectChanged(){
		boolean ret = false;
		synchronized(lock){
			ret = queue.size() > 0;
		}
		return ret;
	}

	public Subject getSubject() {
		return subject;
	}
	
	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	public static void setExternalObserver(ExternalObserver extObserver){
		observer = extObserver;
	}
}
