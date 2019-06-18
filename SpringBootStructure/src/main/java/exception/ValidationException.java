package com.siemens.krawal.krawalcloudmanager.exception;

public class ValidationException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String message;

	public ValidationException(String message){
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
