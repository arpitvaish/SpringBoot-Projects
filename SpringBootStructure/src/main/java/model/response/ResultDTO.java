package com.siemens.krawal.krawalcloudmanager.model.response;

public class ResultDTO {

	private String code;
	private String message;
	private Object response;

	public String getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResponse() {
		return response;
	}
	public void setResponse(Object response) {
		this.response = response;
	}
}
