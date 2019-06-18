package com.siemens.krawal.krawalcloudmanager.model;

public class SignalConnection {

	private String sourceType;
	private int sourceId;
	private String targetType;
	private int targetId;
	private String targetPort;
	private String sourcePort;
	private int krawalId;

	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public int getSourceId() {
		return sourceId;
	}
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}
	public String getTargetType() {
		return targetType;
	}
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	public int getTargetId() {
		return targetId;
	}
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	public String getTargetPort() {
		return targetPort;
	}
	public void setTargetPort(String targetPort) {
		this.targetPort = targetPort;
	}
	public String getSourcePort() {
		return sourcePort;
	}
	public void setSourcePort(String sourcePort) {
		this.sourcePort = sourcePort;
	}
	public int getKrawalId() {
		return krawalId;
	}
	public void setKrawalId(int krawalId) {
		this.krawalId = krawalId;
	}
}
