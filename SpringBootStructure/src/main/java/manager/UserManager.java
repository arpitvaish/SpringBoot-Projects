package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.dao.UserDAO;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.User;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class UserManager {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private DBUtil dbUtil;

	public void createUser(String projectId, List<User> user) {
		if (userDAO.verifyprojectExists(projectId)) {
			Set<String> users = new HashSet<>();
			users.addAll(user.stream().map(e -> e.getGid().toUpperCase()).collect(Collectors.toSet()));
			Map<String, Value> returnedUsers = dbUtil.fetchUsers(users);
			userDAO.createUser(projectId, user, returnedUsers);
		} else {
			throw new DBException("Project with name :" + projectId + " doesn't exists.");
		}
	}

	public void removeUser(String projectId, List<User> usersList) {
		Set<String> users = new HashSet<>();
		users.addAll(usersList.stream().map(e -> e.getGid().toUpperCase()).collect(Collectors.toSet()));
		Map<String, Value> returnedUsers = userDAO.fetchUsersAssociatedWithProject(projectId, users);
		for (User user : usersList) {
			String userId = user.getGid().toUpperCase();
			if (returnedUsers.containsKey(userId)) {
				userDAO.removeUserRelationshipWithProject(projectId, userId);
				userDAO.removeUserInfoNoOtherProjects(projectId, userId);
			} else {
				throw new DBException("User with Id:" + userId + " is not found.");
			}
		}
	}

}
