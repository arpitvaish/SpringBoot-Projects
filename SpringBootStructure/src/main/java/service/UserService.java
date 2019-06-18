package com.siemens.krawal.krawalcloudmanager.service;

import java.util.List;

import com.siemens.krawal.krawalcloudmanager.model.User;

public interface UserService {

	public void createUser(String projectId, List<User> users);

	public void removeUser(String projectId, List<User> users);
}
