package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;

public interface MediumConnectionDAO {

	public void createMediumConnection(String projectName, String crId, int objectCounter, MediumConnection connection,
			String checkoutRevision, String userId);

	public void fetchMediumConnections(ContextVariables contextVariables);

}
