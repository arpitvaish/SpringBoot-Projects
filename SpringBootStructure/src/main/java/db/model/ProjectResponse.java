package com.siemens.krawal.krawalcloudmanager.db.model;

import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

public class ProjectResponse {

	private Value description;
	private Integer latestRevisionNumber;
	private String createdBy;
	private Set<Value> users;
	private List<RevisionResponse> revisions;
	private Value name;
	
	public Integer getLatestRevisionNumber() {
		return latestRevisionNumber;
	}

	public void setLatestRevisionNumber(Integer latestRevisionNumber) {
		this.latestRevisionNumber = latestRevisionNumber;
	}

	public Value getDescription() {
		return description;
	}

	public void setDescription(Value description) {
		this.description = description;
	}

	public Set<Value> getUsers() {
		return users;
	}

	public void setUsers(Set<Value> users) {
		this.users = users;
	}

	public List<RevisionResponse> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<RevisionResponse> revisions) {
		this.revisions = revisions;
	}

	public Value getName() {
		return name;
	}

	public void setName(Value name) {
		this.name = name;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
