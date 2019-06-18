package com.siemens.krawal.krawalcloudmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.service.FetchObjectsService;
import com.siemens.krawal.krawalcloudmanager.util.ServiceUtil;

@Component
public class FetchObjectsServiceImpl implements FetchObjectsService {

	@Autowired
	private ServiceUtil applicationUtil;

	@Override
	public Object fetchStructureObjects(String projectId, String revisionId, String objectType, String userId) {

		ContextVariables contextVariables = setContext(projectId, revisionId, userId);

		LoadObjectManager objectTypesDAO = applicationUtil.findExactExtension(objectType);
		Object result = null;
		if (null != objectTypesDAO) {
			result = objectTypesDAO.fetch(contextVariables);
		}
		return result;
	}

	@Override
	public Object fetchLoadpointObjects(String projectId, String revisionId, int loadpointId, String objectType,
			String userId) {

		ContextVariables contextVariables = setContext(projectId, revisionId, userId);
		contextVariables.setLoadpontId(loadpointId);

		LoadObjectManager objectTypesDAO = applicationUtil.findExactExtension(objectType);
		Object result = null;
		if (null != objectTypesDAO) {
			result = objectTypesDAO.fetch(contextVariables);
		}
		return result;
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
