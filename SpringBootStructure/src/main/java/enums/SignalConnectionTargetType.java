package com.siemens.krawal.krawalcloudmanager.enums;


public enum SignalConnectionTargetType {

	AGGREGATE("Aggregate"),MEDIUMCONNECTION("MediumConnection"),CONSTRAINT("Constraint");

	private String targetType;

	private SignalConnectionTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTargetType(){

		return this.targetType;
	}
}
