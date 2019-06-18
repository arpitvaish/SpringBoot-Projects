package com.siemens.krawal.krawalcloudmanager.enums;


public enum SignalConnectionSourceType {

	CONTROLUNIT("ControlUnit"),CONSTRAINT("Constraint");

	private String sourceType;

	SignalConnectionSourceType(String sourceType)
	{
		this.sourceType = sourceType;
	}
	
	public String getSourceType(){
		
		return this.sourceType;
	}
}
