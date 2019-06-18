package com.siemens.krawal.krawalcloudmanager.service.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.DESIGN_SCOPE;
import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.PLANT_SCOPE;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.manager.DataManager;
import com.siemens.krawal.krawalcloudmanager.model.response.DataResponse;
import com.siemens.krawal.krawalcloudmanager.service.DataService;

@Component
public class DataServiceImpl implements DataService {

	@Autowired
	private DataManager dataManager;

	@Override
	public void setParameters(String projectName, String checkoutRevisionId, String objectType, int objectId,
			Map<String, String> data, String scope, String userId) {

		ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, objectType, objectId, userId);

		if (scope.equalsIgnoreCase(PLANT_SCOPE)) {
			dataManager.setPlantData(contextVariables, data);
		} else if (scope.equalsIgnoreCase(DESIGN_SCOPE)) {
			dataManager.setDesignData(contextVariables, data);
		}
	}

	@Override
	public DataResponse getParameters(String projectName, String checkoutRevisionId, int objectId, String scope,
			String objectType, String userId) {

		ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, objectType, objectId, userId);

		if (scope.equalsIgnoreCase(PLANT_SCOPE)) {
			return dataManager.getPlantData(contextVariables);
		} else if (scope.equalsIgnoreCase(DESIGN_SCOPE)) {
			return dataManager.getDesignData(contextVariables);
		}
		return null;
	}

	@Override
	public void setLoadParameters(String projectName, String checkoutRevisionId, String objectType, int objectId,
			int lpId, Map<String, String> data, String userId) {

		ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, objectType, objectId, userId);
		contextVariables.setLoadpontId(lpId);
		dataManager.setLoadData(contextVariables, data);
	}

	@Override
	public DataResponse getLoadParameters(String projectName, String checkoutRevisionId, int objectId, int lpId,
			String objectType, String userId) {
		ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, objectType, objectId, userId);
		contextVariables.setLoadpontId(lpId);
		return dataManager.getLoadData(contextVariables);
	}

	public ContextVariables setContext(String projectName, String checkoutRevisionId, String objectType, int objectId,
			String userId) {

		ContextVariables contextVariables = new ContextVariables();
		contextVariables.setProjectName(projectName);
		if (checkoutRevisionId.length() > userId.length()) {
			contextVariables.setCheckoutRevisionId(checkoutRevisionId);
		} else {
			contextVariables.setRevisionNumber(Integer.parseInt(checkoutRevisionId));
		}
		contextVariables.setObjectType(objectType);
		contextVariables.setObjectId(objectId);
		contextVariables.setUserId(userId);

		return contextVariables;
	}
}
