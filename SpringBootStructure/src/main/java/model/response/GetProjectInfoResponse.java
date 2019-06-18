package com.siemens.krawal.krawalcloudmanager.model.response;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.db.model.RevisionResponse;

public class GetProjectInfoResponse {

	private String description;
	private Integer latestRevisionNumber;
	private String createdBy;
	private List<String> users;
	private List<RevisionResponse> revisions;

	public Integer getLatestRevisionNumber() {
		return latestRevisionNumber;
	}

	public void setLatestRevisionNumber(Integer latestRevisionNumber) {
		this.latestRevisionNumber = latestRevisionNumber;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public List<RevisionResponse> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<RevisionResponse> revisions) {
		this.revisions = revisions;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
}
