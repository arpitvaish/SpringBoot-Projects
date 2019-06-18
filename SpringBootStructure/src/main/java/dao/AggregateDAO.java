package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface AggregateDAO {

	public void createAggregate(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision);

	public void fetchAggregates(ContextVariables contextVariables);

	public ContextVariables getAssemblyParts(ContextVariables contextVariables);

	public void assemble(ContextVariables contextVariables);
}
