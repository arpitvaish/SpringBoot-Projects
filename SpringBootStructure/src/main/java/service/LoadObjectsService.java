package com.siemens.krawal.krawalcloudmanager.service;

import com.siemens.krawal.krawalcloudmanager.model.CreateLPObjectRequest;
import com.siemens.krawal.krawalcloudmanager.model.CreateObjectRequest;

public interface LoadObjectsService {

	public int createAttributes(String projectName, String crId, CreateObjectRequest request, String objectType,
			String userId);

	public int createLPAttributes(String projectName, String crId, int lpId, CreateLPObjectRequest request,
			String objectType, String userId);

	public void deleteObject(String projectName, String checkoutRevisionid, int objectId, String objectType,
			String userId);

}
