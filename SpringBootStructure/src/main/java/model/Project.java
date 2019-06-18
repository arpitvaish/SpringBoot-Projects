package com.siemens.krawal.krawalcloudmanager.model;

import java.util.List;

public class Project {

	private String description;
	private List<User> users;


	public String getDescription() {
		return description;
	}
	public List<User> getUsers() {
		return users;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}


}
