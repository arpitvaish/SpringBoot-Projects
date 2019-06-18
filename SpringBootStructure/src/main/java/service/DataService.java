package com.siemens.krawal.krawalcloudmanager.service;

import java.util.Map;

import com.siemens.krawal.krawalcloudmanager.model.response.DataResponse;

public interface DataService {

	public void setParameters(String projectName, String checkoutRevisionId, String objectType, int objectId,
			Map<String, String> data, String scope, String userId);

	public DataResponse getParameters(String projectName, String checkoutRevisionId, int objectId, String scope,
			String objectType, String userId);

	public void setLoadParameters(String projectName, String checkoutRevisionId, String objectType, int objectId,
			int lpId, Map<String, String> data, String userId);

	public DataResponse getLoadParameters(String projectName, String checkoutRevisionId, int objectId, int lpId,
			String objectType, String userId);
}
