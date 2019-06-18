package com.siemens.krawal.krawalcloudmanager.context;

import java.util.List;

import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.model.Aggregate;
import com.siemens.krawal.krawalcloudmanager.model.Constraint;
import com.siemens.krawal.krawalcloudmanager.model.ControlUnit;
import com.siemens.krawal.krawalcloudmanager.model.CycleSegment;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;

@Component
public class ContextVariables {

	String projectName;
	String checkoutRevisionId;
	private int objectCounter;

	private Aggregate aggregate;
	private MediumConnection mediumConnection;
	private CycleSegment cycleSegment;

	private ControlUnit controlUnit;
	private Constraint constraint;
	private SignalConnection signalConnection;

	private int loadpontId;
	private String checkoutRevisionIRI;
	private int revisionNumber;

	private int objectId;
	private String objectType;
	private List<Integer> members;
	private String userId;

	private TupleQueryResult result;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getCheckoutRevisionId() {
		return checkoutRevisionId;
	}

	public void setCheckoutRevisionId(String checkoutRevisionId) {
		this.checkoutRevisionId = checkoutRevisionId;
	}

	public int getObjectCounter() {
		return objectCounter;
	}

	public void setObjectCounter(int objectCounter) {
		this.objectCounter = objectCounter;
	}

	public Aggregate getAggregate() {
		return aggregate;
	}

	public void setAggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
	}

	public MediumConnection getMediumConnection() {
		return mediumConnection;
	}

	public void setMediumConnection(MediumConnection mediumConnection) {
		this.mediumConnection = mediumConnection;
	}

	public CycleSegment getCycleSegment() {
		return cycleSegment;
	}

	public void setCycleSegment(CycleSegment cycleSegment) {
		this.cycleSegment = cycleSegment;
	}

	public int getLoadpontId() {
		return loadpontId;
	}

	public void setLoadpontId(int loadpontId) {
		this.loadpontId = loadpontId;
	}

	public String getCheckoutRevisionIRI() {
		return checkoutRevisionIRI;
	}

	public void setCheckoutRevisionIRI(String checkoutRevisionIRI) {
		this.checkoutRevisionIRI = checkoutRevisionIRI;
	}

	public int getRevisionNumber() {
		return revisionNumber;
	}

	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	public int getObjectId() {
		return objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public List<Integer> getMembers() {
		return members;
	}

	public void setMembers(List<Integer> members) {
		this.members = members;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TupleQueryResult getResult() {
		return result;
	}

	public void setResult(TupleQueryResult result) {
		this.result = result;
	}

	public ControlUnit getControlUnit() {
		return controlUnit;
	}

	public Constraint getConstraint() {
		return constraint;
	}

	public SignalConnection getSignalConnection() {
		return signalConnection;
	}

	public void setControlUnit(ControlUnit controlUnit) {
		this.controlUnit = controlUnit;
	}

	public void setConstraint(Constraint constraint) {
		this.constraint = constraint;
	}

	public void setSignalConnection(SignalConnection signalConnection) {
		this.signalConnection = signalConnection;
	}

}
