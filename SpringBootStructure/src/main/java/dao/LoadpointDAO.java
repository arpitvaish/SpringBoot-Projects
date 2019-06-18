package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface LoadpointDAO {

	public void createLoadpoint(String projectName, String crId, int objectCounter, String checkoutRevision);

	public void fetchLoadpoints(ContextVariables contextVariables);
}
