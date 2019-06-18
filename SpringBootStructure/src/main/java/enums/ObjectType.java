package com.siemens.krawal.krawalcloudmanager.enums;


public enum ObjectType {

	AGGREGATE("Aggregate"),CONSTRAINT("Constraint"),MEDIUMCONNECTION("MediumConnection"),CYCLESEGMENT("CycleSegment"),CONTROLUNIT("ControlUnit"),LOADPOINT("Loadpoint"),SIGNALCONNECTION("SignalConnection"),NA("na");
	
	private String objectType;

	ObjectType(String objectType)
	{
		this.objectType = objectType;
	}
	
	public String getObjectType(){
		
		return this.objectType;
	}
}
