package com.siemens.krawal.krawalcloudmanager.model;

public class CreateLPObjectRequest {

	private ControlUnit controlUnit;
	private Constraint constraint;
	private SignalConnection signalConnection;
	
	public ControlUnit getControlUnit() {
		return controlUnit;
	}
	public void setControlUnit(ControlUnit controlUnit) {
		this.controlUnit = controlUnit;
	}
	public Constraint getConstraint() {
		return constraint;
	}
	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}
	public SignalConnection getSignalConnection() {
		return signalConnection;
	}
	public void setSignalConnection(SignalConnection signalConnection) {
		this.signalConnection = signalConnection;
	}	
}
