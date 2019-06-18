package com.siemens.krawal.krawalcloudmanager.exception;

public class NetworkException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String message;

	public NetworkException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}

