package com.siemens.krawal.krawalcloudmanager.service;

public interface FetchObjectsService {

	public Object fetchStructureObjects(String projectId, String revisionId, String objectType, String userId);

	public Object fetchLoadpointObjects(String projectId, String revisionId, int loadpointId, String objectType,
			String userId);
}
