package com.siemens.krawal.krawalcloudmanager.exception;

public class RequestModificationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5633081499761486023L;

	private final String message;

	public RequestModificationException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
