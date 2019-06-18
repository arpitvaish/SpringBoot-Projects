package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.response.KrawalStructureObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.LoadpointObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.KrawalBulkDataService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
@RequestMapping("/pdm")
public class KrawalBulkLoadController {

	@Autowired
	private AuthorizationUtil authUtil;

	@Autowired
	private KrawalBulkDataService kbService;

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{revisionId}/loadpoint/{loadpointId}")
	public Object fetchAllLoadpointObjects(@PathVariable String projectId, @PathVariable String revisionId,
			@PathVariable int loadpointId, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		LoadpointObjectsResponse resp = null;
		if (authUtil.validateCheckoutRevision(userId, revisionId, true))
			resp = kbService.fetchAllLoadpointObjects(projectId, revisionId, userId, loadpointId);
		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(resp);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{revisionId}")
	public Object fetchAllStructureObjects(@PathVariable String projectId, @PathVariable String revisionId,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		KrawalStructureObjectsResponse resp = null;
		if (authUtil.validateCheckoutRevision(userId, revisionId, true))
			resp = kbService.fetchAllStructureObjects(projectId, revisionId, userId);
		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(resp);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
