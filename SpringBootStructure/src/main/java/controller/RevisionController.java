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

import com.siemens.krawal.krawalcloudmanager.model.CommitRevisionRequest;
import com.siemens.krawal.krawalcloudmanager.model.response.CheckoutRevisionResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.CommitRevisionResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.RevisionService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
public class RevisionController {

	@Autowired
	private RevisionService revisionService;

	@Autowired
	private AuthorizationUtil authUtil;
	
	int revisionNo;

	@RequestMapping(method = RequestMethod.POST, value = "/pdm/{projectId}/{revisionId}")
	public ResponseEntity<Object> checkoutRevision(@RequestHeader("Authorization") String authToken,
			@PathVariable String projectId, @PathVariable int revisionId) {

		String userId = authUtil.getUserID(authToken);
		String crId = revisionService.checkoutRevision(projectId, revisionId, userId);
		CheckoutRevisionResponse checkoutRevisionResponse = new CheckoutRevisionResponse();
		checkoutRevisionResponse.setCheckoutRevisionId(crId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.CREATED.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(checkoutRevisionResponse);
		return ResponseEntity.status(201).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/pdm/{projectId}/{revisionId}")
	public ResponseEntity<Object> deleteCheckoutRevision(@RequestHeader("Authorization") String authToken,
			@PathVariable String projectId, @PathVariable String revisionId) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, revisionId, false))
			revisionService.deleteCheckoutRevision(projectId, revisionId, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/pdm/{projectId}/{checkoutRevisionId}")
	public ResponseEntity<Object> commitRevision(@RequestHeader("Authorization") String authToken,
			@PathVariable String projectId, @PathVariable String checkoutRevisionId,
			@RequestBody CommitRevisionRequest request) {

		String userId = authUtil.getUserID(authToken);
		if (authUtil.validateCheckoutRevision(userId, checkoutRevisionId, false))
			revisionNo = revisionService.commitRevision(projectId, checkoutRevisionId, request, userId);

		CommitRevisionResponse commitRevisionResponse = new CommitRevisionResponse();
		commitRevisionResponse.setRevisionNumber(revisionNo);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(commitRevisionResponse);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
