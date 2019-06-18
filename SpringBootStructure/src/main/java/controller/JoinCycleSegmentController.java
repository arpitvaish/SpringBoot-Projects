package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.UpdateCycleSegmentRequest;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.JoinCycleSegmentService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
public class JoinCycleSegmentController {

	@Autowired
	private JoinCycleSegmentService joinCSService;

	@Autowired
	private AuthorizationUtil authUtil;

	@RequestMapping(method = RequestMethod.PUT, value = "/pdm/{projectId}/{checkoutRevisionId}/CycleSegments/{objectId}/members")
	public ResponseEntity<ResultDTO> joinCycleSegment(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable int objectId,
			@RequestBody UpdateCycleSegmentRequest segmentRequest, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			joinCSService.joinCycleSegment(projectId, checkoutRevisionId, objectId, segmentRequest.getMembers(), userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
