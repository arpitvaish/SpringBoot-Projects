package com.siemens.krawal.krawalcloudmanager.service;

import java.util.List;

public interface JoinCycleSegmentService {

	public void joinCycleSegment(String projectName, String checkoutRevisionId, int objectId, List<Integer> members,
			String userId);
}
