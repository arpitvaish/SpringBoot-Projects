package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;

import com.siemens.krawal.krawalcloudmanager.model.User;

public interface UserDAO {

	public void createUser(String projectId, List<User> users, Map<String, Value> returnedUsers);

	public void removeUserRelationshipWithProject(String projectId, String userId);

	public Map<String, Value> fetchUsersAssociatedWithProject(String projectId, Set<String> userIds);

	public void removeUserInfoNoOtherProjects(String projectId, String userId);

	public boolean verifyprojectExists(String projectName);
}
