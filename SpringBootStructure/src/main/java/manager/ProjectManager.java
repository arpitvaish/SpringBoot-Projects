package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.dao.ProjectDAO;
import com.siemens.krawal.krawalcloudmanager.db.model.ProjectResponse;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.Project;
import com.siemens.krawal.krawalcloudmanager.model.response.GetProjectInfoResponse;

@Component
public class ProjectManager {

	@Autowired
	private ProjectDAO projectDAO;

	/**
	 * 
	 * @param project
	 * @param projectName
	 * @param userId
	 */
	public void createProject(Project project, String projectName, String userId) {

		if (projectDAO.verifyprojectExists(projectName)) {
			throw new DBException("Project with name :" + projectName + " already exists. Please choose another name.");
		} else {
			Set<String> users = new HashSet<>();
			if (!CollectionUtils.isEmpty(project.getUsers())) {
				users.addAll(project.getUsers().stream().map(e -> e.getGid().toUpperCase()).collect(Collectors.toSet()));
			}
			Map<String, Value> returnedUsers = projectDAO.fetchUsers(users);
			projectDAO.createProject(project, projectName, returnedUsers,userId);
		}
	}

	/**
	 * 
	 * @param projectName
	 * @param userId
	 */
	public void deleteProject(String projectName, String userId) {

		if (projectDAO.verifyUserAccessToProject(projectName, userId)) {
			projectDAO.deleteProject(projectName, userId);
		} else {
			throw new DBException("Project is not found or permission is denied for user!");
		}
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getAllProjects(String userId) {

		List<String> projects = new ArrayList<>();
		List<ProjectResponse> resp = projectDAO.getAllProjects(userId);
		if (!CollectionUtils.isEmpty(resp)) {
			return resp.stream().map(e -> e.getName().stringValue()).collect(Collectors.toList());
		}
		return projects;
	}

	/**
	 * 
	 * @param projectName
	 * @param userId
	 * @return
	 */
	public GetProjectInfoResponse getProjectInformation(String projectName, String userId) {

		GetProjectInfoResponse resp = new GetProjectInfoResponse();

		if (projectDAO.verifyUserAccessToProject(projectName, userId)) {
			ProjectResponse response = projectDAO.getProjectInformation(projectName, userId);
			resp.setDescription(response.getDescription().stringValue());
			resp.setLatestRevisionNumber(response.getLatestRevisionNumber());
			resp.setRevisions(response.getRevisions());
			resp.setCreatedBy(response.getCreatedBy());
			resp.setUsers(response.getUsers().stream().map(e -> e.stringValue()).collect(Collectors.toList()));
		} else {
			throw new DBException("Project with name :" + projectName
					+ " doesnot exists OR user does not have permission to access. Please verify.");
		}
		return resp;
	}
}
