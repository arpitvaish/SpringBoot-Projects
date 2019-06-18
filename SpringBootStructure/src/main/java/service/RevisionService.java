package com.siemens.krawal.krawalcloudmanager.service;

import com.siemens.krawal.krawalcloudmanager.model.CommitRevisionRequest;

public interface RevisionService {

	public String checkoutRevision(String projectId, int revisionNumber, String userId);

	public void deleteCheckoutRevision(String projectId, String revisionNumber, String userId);

	public int commitRevision(String projectName, String crId, CommitRevisionRequest request, String userId);
}
