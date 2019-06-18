package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;

public interface SignalConnectionDAO {

	public void createSignalConnection(String projectName, String crId, int objectCounter, String checkoutRevision,
			int lpId, SignalConnection connection, String userId);

	public void fetchSignalConnections(ContextVariables contextVariables);
}
