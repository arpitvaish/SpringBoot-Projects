package com.siemens.krawal.krawalcloudmanager.db.model;

public class RevisionResponse {
	
	private String revisionNo;
	private String comment;
	private String committedBy;

	public String getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(String revisionNo) {
		this.revisionNo = revisionNo;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCommittedBy() {
		return committedBy;
	}

	public void setCommittedBy(String committedBy) {
		this.committedBy = committedBy;
	}
}
