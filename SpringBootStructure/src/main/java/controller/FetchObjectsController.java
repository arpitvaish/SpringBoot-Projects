package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.FetchObjectsService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
@RequestMapping("/pdm")
public class FetchObjectsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FetchObjectsController.class);
	
	@Autowired
	private FetchObjectsService fetchObjectsService;

	@Autowired
	private AuthorizationUtil authUtil;
	
	Object finalResult;

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{revisionId}/{objectType}")
	public Object fetchStructureAttributes(@PathVariable String projectId, @PathVariable String revisionId,
			@PathVariable String objectType, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, revisionId, true))
			finalResult = fetchObjectsService.fetchStructureObjects(projectId, revisionId, objectType, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(finalResult);
		LOGGER.info("exit -> method -> fetch"+objectType.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);

	}

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{revisionId}/loadpoint/{loadpointId}/{objectType}")
	public Object fetchLoadpointAttributes(@PathVariable String projectId, @PathVariable String revisionId,
			@PathVariable int loadpointId, @PathVariable String objectType,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, revisionId, true))
			finalResult = fetchObjectsService.fetchLoadpointObjects(projectId, revisionId, loadpointId, objectType, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(finalResult);
		LOGGER.info("exit -> method -> fetch"+objectType.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);

	}
}
