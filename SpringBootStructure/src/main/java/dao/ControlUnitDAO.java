package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface ControlUnitDAO {

	public void createControlUnit(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision, int lpId);

	public void fetchControlUnits(ContextVariables contextVariables);
}
