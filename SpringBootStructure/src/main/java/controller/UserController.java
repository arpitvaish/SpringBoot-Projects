package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.SUCCESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.CreateUser;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.UserService;

@RestController
@RequestMapping("/pdm")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.POST, value = "/{projectId}/users")
	public ResponseEntity<Object> createUser(@PathVariable String projectId, @RequestBody CreateUser users) {

		userService.createUser(projectId, users.getUsers());

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{projectId}/users")
	public ResponseEntity<Object> removeUser(@PathVariable String projectId, @RequestBody CreateUser users) {

		userService.removeUser(projectId, users.getUsers());

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(200).body(resultDTO);
	}
}
