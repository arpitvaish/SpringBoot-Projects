package com.siemens.krawal.krawalcloudmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.manager.CycleSegmentManager;
import com.siemens.krawal.krawalcloudmanager.service.JoinCycleSegmentService;

@Component
public class JoinCycleSegmentServiceImpl implements JoinCycleSegmentService {

	@Autowired
	private CycleSegmentManager csManager;

	@Override
	public void joinCycleSegment(String projectName, String checkoutRevisionId, int objectId, List<Integer> members,
			String userId) {

		ContextVariables contextVariables = new ContextVariables();
		contextVariables.setCheckoutRevisionId(checkoutRevisionId);
		contextVariables.setProjectName(projectName);
		contextVariables.setObjectId(objectId);
		contextVariables.setMembers(members);
		contextVariables.setUserId(userId);
		csManager.joinCycleSegment(contextVariables);

	}

}
