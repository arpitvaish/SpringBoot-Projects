package com.siemens.krawal.krawalcloudmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.manager.RevisionManager;
import com.siemens.krawal.krawalcloudmanager.model.CommitRevisionRequest;
import com.siemens.krawal.krawalcloudmanager.service.RevisionService;

@Component
public class RevisionServiceImpl implements RevisionService {

	@Autowired
	private RevisionManager revisionManager;

	@Override
	public String checkoutRevision(String projectId, int revisionNumber, String userId) {
		return revisionManager.checkoutRevision(projectId, revisionNumber, userId);
	}

	@Override
	public void deleteCheckoutRevision(String projectId, String revisionNumber, String userId) {
		revisionManager.deleteCheckoutRevision(projectId, revisionNumber, userId);
	}

	@Override
	public int commitRevision(String projectName, String crId, CommitRevisionRequest request, String userId) {	
		return revisionManager.commitRevision(projectName, crId, userId, request);
	}

}
