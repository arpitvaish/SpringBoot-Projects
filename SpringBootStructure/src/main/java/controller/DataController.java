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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.DataControllerRequest;
import com.siemens.krawal.krawalcloudmanager.model.response.DataResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.DataService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
@RequestMapping("/pdm")
public class DataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

	@Autowired
	private DataService dataService;

	@Autowired
	private AuthorizationUtil authUtil;
	
	DataResponse response;

	@RequestMapping(method = RequestMethod.PUT, value = "{projectId}/{checkoutRevisionId}/{objectType}/{objectId}")
	public ResponseEntity<Object> setPlantParameters(@PathVariable String projectId, @PathVariable String checkoutRevisionId,
			@PathVariable int objectId, @PathVariable String objectType, @RequestParam("scope") String scope,
			@RequestBody DataControllerRequest request, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			dataService.setParameters(projectId, checkoutRevisionId, objectType, objectId, request.getData(), scope, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		LOGGER.info("exit -> method -> set"+scope.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{checkoutRevisionId}/{objectType}/{objectId}")
	public ResponseEntity<Object> getPlantParameters(@PathVariable String projectId, @PathVariable String checkoutRevisionId,
			@PathVariable int objectId, @PathVariable String objectType, @RequestParam("scope") String scope,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, true))
			response = dataService.getParameters(projectId, checkoutRevisionId, objectId, scope, objectType, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(response);
		LOGGER.info("exit -> method -> get"+scope.toLowerCase());
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/{checkoutRevisionId}/loadpoint/{lpId}/{objectType}/{objectId}")
	public ResponseEntity<Object> setLoadParameters(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable int objectId, @PathVariable String objectType,
			@PathVariable int lpId, @RequestBody DataControllerRequest request,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			dataService.setLoadParameters(projectId, checkoutRevisionId, objectType, objectId, lpId, request.getData(), userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{projectId}/{checkoutRevisionId}/loadpoint/{lpId}/{objectType}/{objectId}")
	public ResponseEntity<Object> getLoadParameters(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable int objectId, @PathVariable String objectType,
			@PathVariable int lpId, @RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, true))
			response = dataService.getLoadParameters(projectId, checkoutRevisionId, objectId, lpId, objectType, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(response);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
