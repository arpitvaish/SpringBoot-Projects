package com.siemens.krawal.krawalcloudmanager.model;

public class CreateObjectRequest {

	private Aggregate aggregate;
	private MediumConnection mediumConnection;
	private CycleSegment cycleSegment;

	public Aggregate getAggregate() {
		return aggregate;
	}
	public MediumConnection getMediumConnection() {
		return mediumConnection;
	}
	public CycleSegment getCycleSegment() {
		return cycleSegment;
	}
	public void setAggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
	}
	public void setMediumConnection(MediumConnection mediumConnection) {
		this.mediumConnection = mediumConnection;
	}
	public void setCycleSegment(CycleSegment cycleSegment) {
		this.cycleSegment = cycleSegment;
	}
}
