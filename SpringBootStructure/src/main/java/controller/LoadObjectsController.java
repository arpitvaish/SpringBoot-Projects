package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.CreateLPObjectRequest;
import com.siemens.krawal.krawalcloudmanager.model.CreateObjectRequest;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.model.response.StagingCreateResponse;
import com.siemens.krawal.krawalcloudmanager.service.LoadObjectsService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
@RequestMapping("/pdm")
public class LoadObjectsController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadObjectsController.class);

	@Autowired
	private LoadObjectsService objectsService;

	@Autowired
	private AuthorizationUtil authUtil;
	
	int id;

	@RequestMapping(method = RequestMethod.POST, value = "/{projectId}/{checkoutRevisionId}/{objectType}")
	public ResponseEntity<Object> stageCreateAttributes(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable String objectType,
			@RequestBody CreateObjectRequest stagingRequest, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			id = objectsService.createAttributes(projectId, checkoutRevisionId, stagingRequest, objectType,userId);

		StagingCreateResponse createResponse = new StagingCreateResponse();
		createResponse.setKrawlId(id);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(createResponse);
		LOGGER.info("exit -> method -> create"+objectType.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{projectId}/{crId}/loadpoint/{loadpointId}/{objectType}")
	public ResponseEntity<Object> stageCreateLPAttributes(@PathVariable String projectId, @PathVariable String crId,
			@PathVariable String objectType, @PathVariable int loadpointId,
			@RequestBody CreateLPObjectRequest stagingRequest, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, crId, false))
			id = objectsService.createLPAttributes(projectId, crId, loadpointId, stagingRequest, objectType,userId);
		
		StagingCreateResponse createResponse = new StagingCreateResponse();
		createResponse.setKrawlId(id);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(createResponse);
		LOGGER.info("exit -> method -> create"+objectType.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{projectId}/{checkoutRevisionId}/{objectType}/{objectId}")
	public ResponseEntity<Object> stageDeleteObject(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable int objectId, @PathVariable String objectType,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			objectsService.deleteObject(projectId, checkoutRevisionId, objectId, objectType, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		LOGGER.info("exit -> method -> delete"+objectType.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);
	}
}
