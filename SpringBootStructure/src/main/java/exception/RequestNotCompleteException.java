package com.siemens.krawal.krawalcloudmanager.exception;

public class RequestNotCompleteException extends RuntimeException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public RequestNotCompleteException(String message){
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
