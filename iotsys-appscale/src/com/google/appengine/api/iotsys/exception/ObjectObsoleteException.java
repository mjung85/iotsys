package com.google.appengine.api.iotsys.exception;

public class ObjectObsoleteException extends CommunicationException {

	private static final long serialVersionUID = 1L;
	
	public ObjectObsoleteException(String message) {
		super(message);
	}

}
