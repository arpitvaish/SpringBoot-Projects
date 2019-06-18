package com.siemens.krawal.krawalcloudmanager.dao;

import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

@Component
public interface ConstraintDAO {

	public void createConstraint(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision, int lpId, String userId);

	public void fetchConstraints(ContextVariables contextVariables);
	public void createReferenceToLpConstraint(ContextVariables contextVariables, int referenceLP, int counter);
}
