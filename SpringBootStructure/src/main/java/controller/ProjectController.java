package com.siemens.krawal.krawalcloudmanager.controller;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.siemens.krawal.krawalcloudmanager.model.Project;
import com.siemens.krawal.krawalcloudmanager.model.response.GetAllProjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.GetProjectInfoResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.ResultDTO;
import com.siemens.krawal.krawalcloudmanager.service.ProjectService;
import com.siemens.krawal.krawalcloudmanager.util.AuthorizationUtil;

@RestController
@RequestMapping("/pdm")
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private AuthorizationUtil authUtil;

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		return ResponseEntity.ok("Service is up and running!!");
	}

	@RequestMapping(method = RequestMethod.POST, value = "/projects/{projectId}")
	public ResponseEntity<Object> createProject(@PathVariable String projectId, @RequestBody Project project,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);
		projectService.createProject(project, projectId, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.CREATED.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(201).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/projects/{projectId}")
	public ResponseEntity<Object> deleteProject(@PathVariable String projectId,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		projectService.deleteProject(projectId, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		return ResponseEntity.status(201).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects/")
	public ResponseEntity<Object> getAllProjects(@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		List<String> response = projectService.getAllProjects(userId);

		GetAllProjectsResponse allProjectsResponse = new GetAllProjectsResponse();
		allProjectsResponse.setProjects(response);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(allProjectsResponse);
		return ResponseEntity.status(200).body(resultDTO);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/projects/{projectId}")
	public ResponseEntity<Object> getProjectInformation(@PathVariable String projectId,
			@RequestHeader("Authorization") String authToken) {

		String userId = authUtil.getUserID(authToken);

		GetProjectInfoResponse response = projectService.getProjectInformation(projectId, userId);

		ResultDTO resultDTO = new ResultDTO();
		resultDTO.setCode(HttpStatus.OK.toString());
		resultDTO.setMessage(SUCCESS);
		resultDTO.setResponse(response);
		return ResponseEntity.status(200).body(resultDTO);
	}

}
