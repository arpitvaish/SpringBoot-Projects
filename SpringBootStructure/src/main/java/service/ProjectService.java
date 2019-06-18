package com.siemens.krawal.krawalcloudmanager.service;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.model.Project;
import com.siemens.krawal.krawalcloudmanager.model.response.GetProjectInfoResponse;

public interface ProjectService {

	public void createProject(Project project, String projectName, String userId);

	public void deleteProject(String projectName, String userId);

	public List<String> getAllProjects(String userId);

	public GetProjectInfoResponse getProjectInformation(String projectName, String userId);
}
