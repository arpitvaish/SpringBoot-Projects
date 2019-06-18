package com.siemens.krawal.krawalcloudmanager.service;

import com.siemens.krawal.krawalcloudmanager.model.response.KrawalStructureObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.LoadpointObjectsResponse;

public interface KrawalBulkDataService {

	public LoadpointObjectsResponse fetchAllLoadpointObjects(String projectId, String revisionId, String userId,
			int loadpointId);

	public KrawalStructureObjectsResponse fetchAllStructureObjects(String projectId, String revisionId, String userId);

}
