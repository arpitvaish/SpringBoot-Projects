package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

import com.siemens.krawal.krawalcloudmanager.db.model.ProjectResponse;
import com.siemens.krawal.krawalcloudmanager.model.Project;

public interface ProjectDAO {

	public boolean verifyprojectExists(String projectName);

	public Map<String, Value> fetchUsers(Set<String> users);

	public boolean createProject(Project project, String projectName, Map<String, Value> returnedUsers, String userId);

	public boolean verifyUserAccessToProject(String projectName, String userId);

	public void deleteProject(String projectName, String userId);

	public List<ProjectResponse> getAllProjects(String userId);

	public ProjectResponse getProjectInformation(String projectName, String userId);
}
