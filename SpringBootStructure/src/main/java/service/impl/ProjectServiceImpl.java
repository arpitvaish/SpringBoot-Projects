package com.siemens.krawal.krawalcloudmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.manager.ProjectManager;
import com.siemens.krawal.krawalcloudmanager.model.Project;
import com.siemens.krawal.krawalcloudmanager.model.response.GetProjectInfoResponse;
import com.siemens.krawal.krawalcloudmanager.service.ProjectService;

@Component
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectManager projectManager;

	@Override
	public void createProject(Project project, String projectName, String userId) {

		if (null != project && null != project.getDescription() && !CollectionUtils.isEmpty(project.getUsers())) {
			projectManager.createProject(project, projectName, userId);
		} else {
			throw new RequestNotCompleteException("Required parameters are not present in the request - description,users.");
		}
	}

	@Override
	public void deleteProject(String projectName, String userId) {

		projectManager.deleteProject(projectName, userId);
	}

	@Override
	public List<String> getAllProjects(String userId) {
		return projectManager.getAllProjects(userId);
	}

	@Override
	public GetProjectInfoResponse getProjectInformation(String projectName, String userId) {

		return projectManager.getProjectInformation(projectName, userId);
	}

}
