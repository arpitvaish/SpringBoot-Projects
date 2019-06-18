package com.siemens.krawal.krawalcloudmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.model.CreateLPObjectRequest;
import com.siemens.krawal.krawalcloudmanager.model.CreateObjectRequest;
import com.siemens.krawal.krawalcloudmanager.service.LoadObjectsService;
import com.siemens.krawal.krawalcloudmanager.util.ServiceUtil;

@Component
public class LoadObjectsServiceImpl implements LoadObjectsService {

	@Autowired
	private ServiceUtil applicationUtil;

	@Autowired
	private LoadObjectManager loadObjectManager;

	@Override
	public int createAttributes(String projectName, String crId, CreateObjectRequest request, String objectType,
			String userId) {

		ContextVariables contextVariables = setContext(projectName, crId, userId);
		LoadObjectManager objectTypesDAO = null;
		int objectId = 0;

		if (null != request) {
			if (null != request.getAggregate()) {
				contextVariables.setAggregate(request.getAggregate());
			} else if (null != request.getCycleSegment()) {
				contextVariables.setCycleSegment(request.getCycleSegment());
			} else if (null != request.getMediumConnection()) {
				contextVariables.setMediumConnection(request.getMediumConnection());
			}
		}

		objectTypesDAO = applicationUtil.findExactExtension(objectType);
		if (null != objectTypesDAO) {
			objectId = objectTypesDAO.create(contextVariables);
		}
		return objectId;
	}

	public ContextVariables setContext(String projectName, String crId, String userId) {
		ContextVariables contextVariables = new ContextVariables();
		contextVariables.setProjectName(projectName);
		contextVariables.setCheckoutRevisionId(crId);
		contextVariables.setUserId(userId);
		return contextVariables;
	}

	@Override
	public int createLPAttributes(String projectName, String crId, int lpId, CreateLPObjectRequest request,
			String objectType, String userId) {

		ContextVariables contextVariables = setContext(projectName, crId, userId);
		contextVariables.setLoadpontId(lpId);
		int objectId = 0;

		if (null != request.getControlUnit()) {
			contextVariables.setControlUnit(request.getControlUnit());
		} else if (null != request.getSignalConnection()) {
			contextVariables.setSignalConnection(request.getSignalConnection());
		} else if (null != request.getConstraint()) {
			contextVariables.setConstraint(request.getConstraint());
		}
		LoadObjectManager objectManager = applicationUtil.findExactExtension(objectType);

		if (null != objectManager) {
			objectId = objectManager.create(contextVariables);
		}
		return objectId;
	}

	@Override
	public void deleteObject(String projectName, String checkoutRevisionid, int objectId, String objectType,
			String userId) {
		loadObjectManager.delete(projectName, checkoutRevisionid, objectId, objectType, userId);
	}

}
