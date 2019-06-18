package com.siemens.krawal.krawalcloudmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.manager.KrawalBulkLoadManager;
import com.siemens.krawal.krawalcloudmanager.model.response.KrawalStructureObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.LoadpointObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.service.KrawalBulkDataService;

@Component
public class KrawalBulkDataServiceImpl implements KrawalBulkDataService {

	@Autowired
	private KrawalBulkLoadManager loadManager;

	@Override
	public LoadpointObjectsResponse fetchAllLoadpointObjects(String projectId, String revisionId, String userId,
			int loadpointId) {
		ContextVariables contextVariables = setContext(projectId, revisionId, userId);
		contextVariables.setLoadpontId(loadpointId);
		return loadManager.fetchAllLoadpointObjects(contextVariables);
	}

	@Override
	public KrawalStructureObjectsResponse fetchAllStructureObjects(String projectId, String revisionId, String userId) {
		ContextVariables contextVariables = setContext(projectId, revisionId, userId);
		return loadManager.fetchAllStuctureObjects(contextVariables);
	}

	public ContextVariables setContext(String projectId, String revisionId, String userId) {

		ContextVariables contextVariables = new ContextVariables();
		contextVariables.setUserId(userId);
		contextVariables.setProjectName(projectId);
		if (revisionId.length() > userId.length()) {
			contextVariables.setCheckoutRevisionId(revisionId);
		} else {
			contextVariables.setRevisionNumber(Integer.parseInt(revisionId));
		}

		return contextVariables;
	}

}
