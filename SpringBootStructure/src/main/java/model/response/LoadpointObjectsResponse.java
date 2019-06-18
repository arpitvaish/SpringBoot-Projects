package com.siemens.krawal.krawalcloudmanager.model.response;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.model.Constraint;
import com.siemens.krawal.krawalcloudmanager.model.ControlUnit;
import com.siemens.krawal.krawalcloudmanager.model.DataDTO;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;

public class LoadpointObjectsResponse {

	private List<ControlUnit> controlUnits;
	private List<SignalConnection> signalConnections;
	private List<Constraint> constraints;
	private List<DataDTO> data;
	
	public List<ControlUnit> getControlUnits() {
		return controlUnits;
	}
	public void setControlUnits(List<ControlUnit> controlUnits) {
		this.controlUnits = controlUnits;
	}
	public List<SignalConnection> getSignalConnections() {
		return signalConnections;
	}
	public void setSignalConnections(List<SignalConnection> signalConnections) {
		this.signalConnections = signalConnections;
	}
	public List<Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}
	public List<DataDTO> getData() {
		return data;
	}
	public void setData(List<DataDTO> data) {
		this.data = data;
	}
}
