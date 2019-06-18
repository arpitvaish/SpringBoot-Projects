package com.siemens.krawal.krawalcloudmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.manager.UserManager;
import com.siemens.krawal.krawalcloudmanager.model.User;
import com.siemens.krawal.krawalcloudmanager.service.UserService;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserManager userManager;

	@Override
	public void createUser(String projectId, List<User> users) {

		if (!CollectionUtils.isEmpty(users)) {
			userManager.createUser(projectId, users);
		} else {
			throw new RequestNotCompleteException("Required parameters are missing in the request - users");
		}
	}

	@Override
	public void removeUser(String projectId, List<User> users) {
		if (!CollectionUtils.isEmpty(users)) {
			userManager.removeUser(projectId, users);
		} else {
			throw new RequestNotCompleteException("Required parameters are missing in the request - Gid");
		}
	}

}
