package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface CycleSegmentDAO {

	public void createCycleSegment(String projectName, String crId, int objectCounter, List<Integer> aggregates,
			String checkoutRevision, String userId);

	public void fetchCycleSegments(ContextVariables contextVariables);

	public void joinCycleSegment(ContextVariables contextVariables);
}
