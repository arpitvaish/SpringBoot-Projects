package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.AssemblyRequest;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.AggregateAssemblyService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
public class AggregateAssemblyController {

	@Autowired
	private AggregateAssemblyService aggregateAssemblyService;

	@Autowired
	private AuthorizationUtil authUtil;

	@RequestMapping(method = RequestMethod.PUT, value = "/pdm/{projectId}/{checkoutRevisionId}/Aggregate/{objectId}/parts")
	public ResponseEntity<ResultDTO> assemble(@PathVariable String projectId, @PathVariable String checkoutRevisionId,
			@PathVariable int objectId, @RequestBody AssemblyRequest assemblyRequest,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			aggregateAssemblyService.assemble(projectId, checkoutRevisionId, objectId, assemblyRequest.getParts(), userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/pdm/{projectId}/{checkoutRevisionId}/Aggregate/{objectId}/parts")
	public ResponseEntity<ResultDTO> getAssemblyParts(@PathVariable String projectId,
			@PathVariable String checkoutRevisionId, @PathVariable int objectId,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		Set<Integer> aggregates = null;
		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, true))
			aggregates = aggregateAssemblyService.getAssemblyParts(projectId, checkoutRevisionId, objectId, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(aggregates);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
