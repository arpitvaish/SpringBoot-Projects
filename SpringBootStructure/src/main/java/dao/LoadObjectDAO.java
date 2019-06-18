package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface LoadObjectDAO {

	public void verifyLoadpoint(ContextVariables contextVariables);

	public void deleteObject(String projectName, int objectId, String crId, String objectType, String userId);

	public void validateProjectAndReturnCounter(ContextVariables contextVariables);

	public String verifyRevision(String projectName, String checkoutRevisionId, int revisionNo, String userId);
}
