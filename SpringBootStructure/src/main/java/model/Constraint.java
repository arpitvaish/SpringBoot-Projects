package com.siemens.krawal.krawalcloudmanager.model;

public class Constraint {

	private int krawalId;
	private String type;
	private int refernceLoadpoint;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int getRefernceLoadpoint() {
		return refernceLoadpoint;
	}

	public void setRefernceLoadpoint(int refernceLoadpoint) {
		this.refernceLoadpoint = refernceLoadpoint;
	}

	public int getKrawalId() {
		return krawalId;
	}

	public void setKrawalId(int krawalId) {
		this.krawalId = krawalId;
	}
}
