package com.siemens.krawal.krawalcloudmanager.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.manager.AggregateManager;
import com.siemens.krawal.krawalcloudmanager.service.AggregateAssemblyService;

@Component
public class AggregateAssemblyServiceImpl implements AggregateAssemblyService {

	@Autowired
	private AggregateManager aggregateManager;

	@Override
	public void assemble(String projectName, String checkoutRevisionId, int aggregateId, List<Integer> parts,
			String userId) {

		if (!CollectionUtils.isEmpty(parts)) {
			ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, aggregateId, userId);
			contextVariables.setMembers(parts);
			aggregateManager.assemble(contextVariables);
		} else {
			throw new RequestNotCompleteException("Required parameters are missing - aggregateParts.");
		}

	}

	@Override
	public Set<Integer> getAssemblyParts(String projectName, String checkoutRevisionId, int aggregateId,
			String userId) {

		ContextVariables contextVariables = setContext(projectName, checkoutRevisionId, aggregateId, userId);
		if (checkoutRevisionId.length() < userId.length()) {
			contextVariables.setCheckoutRevisionId(null);
			contextVariables.setRevisionNumber(Integer.parseInt(checkoutRevisionId));
		}
		return aggregateManager.getAssembleParts(contextVariables);
	}

	private ContextVariables setContext(String projectName, String checkoutRevisionId, int aggregateId, String userId) {

		ContextVariables contextVariables = new ContextVariables();
		contextVariables.setCheckoutRevisionId(checkoutRevisionId);
		contextVariables.setProjectName(projectName);
		contextVariables.setObjectId(aggregateId);
		contextVariables.setUserId(userId);

		return contextVariables;
	}
}
