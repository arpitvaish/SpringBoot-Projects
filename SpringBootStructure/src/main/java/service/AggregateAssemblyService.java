package com.siemens.krawal.krawalcloudmanager.service;

import java.util.List;
import java.util.Set;

public interface AggregateAssemblyService {

	public void assemble(String projectName, String checkoutRevisionId, int aggregateId, List<Integer> parts,
			String userId);

	public Set<Integer> getAssemblyParts(String projectName, String checkoutRevisionId, int aggregateId, String userId);

}
