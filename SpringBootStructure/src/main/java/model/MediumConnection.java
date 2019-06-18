package com.siemens.krawal.krawalcloudmanager.model;

public class MediumConnection {

	private String type;
	private int sourceAggregateId;
	private String destinationPort;
	private int destinationAggregateId;
	private int krawalId;
	private String sourcePort;

	public String getType() {
		return type;
	}
	public int getSourceAggregateId() {
		return sourceAggregateId;
	}
	public String getDestinationPort() {
		return destinationPort;
	}
	public int getDestinationAggregateId() {
		return destinationAggregateId;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setSourceAggregateId(int sourceAggregateId) {
		this.sourceAggregateId = sourceAggregateId;
	}
	public void setDestinationPort(String destinationPort) {
		this.destinationPort = destinationPort;
	}
	public void setDestinationAggregateId(int destinationAggregateId) {
		this.destinationAggregateId = destinationAggregateId;
	}
	public int getKrawalId() {
		return krawalId;
	}
	public void setKrawalId(int krawalId) {
		this.krawalId = krawalId;
	}
	public String getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}
}
