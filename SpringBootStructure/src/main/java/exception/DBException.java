package com.siemens.krawal.krawalcloudmanager.exception;

public class DBException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2218976083456846949L;

	private final String message;

	public DBException(String message) {
		super();
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
