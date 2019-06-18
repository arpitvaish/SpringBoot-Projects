package com.siemens.krawal.krawalcloudmanager.model.response;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.model.Aggregate;
import com.siemens.krawal.krawalcloudmanager.model.CycleSegment;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;

public class KrawalStructureObjectsResponse {

	private List<Aggregate> aggregates;
	private List<MediumConnection> mediumConnections;
	private List<CycleSegment> cycleSegments;
	
	public List<Aggregate> getAggregates() {
		return aggregates;
	}
	public void setAggregates(List<Aggregate> aggregates) {
		this.aggregates = aggregates;
	}
	public List<MediumConnection> getMediumConnections() {
		return mediumConnections;
	}
	public void setMediumConnections(List<MediumConnection> mediumConnections) {
		this.mediumConnections = mediumConnections;
	}
	public List<CycleSegment> getCycleSegments() {
		return cycleSegments;
	}
	public void setCycleSegments(List<CycleSegment> cycleSegments) {
		this.cycleSegments = cycleSegments;
	}
	
	
}
