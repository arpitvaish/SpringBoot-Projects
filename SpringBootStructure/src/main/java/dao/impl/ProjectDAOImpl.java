package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED_BY;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_DESCRIPTION;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_LATEST_REVISION_NUMBER;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_NAME;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_OBJECT_COUNTER;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_USER;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.DELETE_PROJECT;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.GET_ALL_PROJECTS;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.GET_PROJECT_INFO;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.VERIFY_PROJECT_TUPLE;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.VERIFY_PROJECT_USER_ACCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.dao.ProjectDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants;
import com.siemens.krawal.krawalcloudmanager.db.model.ProjectResponse;
import com.siemens.krawal.krawalcloudmanager.db.model.RevisionResponse;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.Project;
import com.siemens.krawal.krawalcloudmanager.model.User;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class ProjectDAOImpl implements ProjectDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public boolean verifyprojectExists(String projectName) {

		String query = dbUtil.formatQuery(VERIFY_PROJECT_TUPLE, false, projectName);
		return finalRepo.booleanQuery(query);
	}

	@Override
	public Map<String, Value> fetchUsers(Set<String> users) {

		return dbUtil.fetchUsers(users);

	}

	@Override
	public boolean createProject(Project project, String projectName, Map<String, Value> returnedUsers, String userId) {

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(USER, USER_NAMESPACE).setNamespace(RDF.NS);

		builder.subject(ROOT_SUBJECT + projectName).add(ROOT_SUBJECT + HAS_NAME, projectName)
				.add(ROOT_SUBJECT + HAS_DESCRIPTION, project.getDescription()).add(ROOT_SUBJECT + HAS_OBJECT_COUNTER, 0)
				.add(ROOT_SUBJECT + HAS_LATEST_REVISION_NUMBER, 0).add(ROOT_SUBJECT + CREATED_BY, userId);

		addUsersToProject(project, projectName, returnedUsers, builder);

		finalRepo.buildModel(builder);
		return true;
	}

	private void addUsersToProject(Project project, String projectName, Map<String, Value> returnedUsers,
			ModelBuilder builder) {

		List<User> users = project.getUsers();
		Iterator<User> iterator = users.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.getGid() != null && user.getUserName() != null) {
				String userId = user.getGid().toUpperCase();
				Value userValue = returnedUsers.get(userId);
				if (userValue != null) {
					builder.subject(ROOT_SUBJECT + projectName).add(ROOT_SUBJECT + HAS_USER, userValue);
				} else {
					builder.subject(USER_ID + userId).add(USER_ID + RelationshipConstants.HAS_GID, userId);
					builder.subject(USER_ID + userId).add(USER_ID + RelationshipConstants.HAS_USERNAME,
							user.getUserName());

					builder.subject(ROOT_NAMESPACE + projectName).add(ROOT_NAMESPACE + HAS_USER,
							finalRepo.getValueFactory().createIRI("", USER_NAMESPACE + userId));
				}
			} else {
				throw new RequestNotCompleteException(
						"Required parameters are not present in the request - Gid (or) UserName.");
			}
		}
	}

	@Override
	public boolean verifyUserAccessToProject(String projectName, String userId) {

		String query = dbUtil.formatQuery(VERIFY_PROJECT_USER_ACCESS, false, projectName, userId);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			Set<String> bindings = null;
			while (result.hasNext()) {

				BindingSet bindingSet = result.next();
				bindings = bindingSet.getBindingNames();
			}
			if (!CollectionUtils.isEmpty(bindings)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void deleteProject(String projectName, String userId) {

		Object[] args = new String[] { projectName, projectName, projectName, projectName };
		String query = dbUtil.formatQuery(DELETE_PROJECT, false, args);
		finalRepo.updateQuery(query);
	}

	@Override
	public List<ProjectResponse> getAllProjects(String userId) {

		List<ProjectResponse> projects = new ArrayList<>();
		String query = dbUtil.formatQuery(GET_ALL_PROJECTS, false, userId);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value projectValue = bindingSet.getValue(QUERYVARIABLE_PROJECTS);
				ProjectResponse projectResponse = new ProjectResponse();
				projectResponse.setName(projectValue);
				projects.add(projectResponse);
			}
		}
		return projects;
	}

	@Override
	public ProjectResponse getProjectInformation(String projectName, String userId) {

		ProjectResponse response = new ProjectResponse();
		Set<Value> users = new HashSet<>();
		Map<String, RevisionResponse> revisionMap = new HashMap<>();
		Value description = null;
		Value latestRevisionNumber = null;
		Value createdBy = null;
		List<RevisionResponse> revisions = new ArrayList<>();
		String query = dbUtil.formatQuery(GET_PROJECT_INFO, false, projectName, userId, projectName);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value userValue = bindingSet.getValue(QUERYVARIABLE_GID);
				description = bindingSet.getValue(QUERYVARIABLE_DESCRIPTION);
				latestRevisionNumber = bindingSet.getValue(QUERYVARIABLE_LRNUMBER);
				Value revisionValue = bindingSet.getValue(QUERYVARIABLE_REVISIONS);
				Value checkoutRevisionValue = bindingSet.getValue(QUERYVARIABLE_CHECKOUT_REVISIONS);
				Value comment = bindingSet.getValue(QUERYVARIABLE_COMMENT);
				Value committedBy = bindingSet.getValue(QUERYVARIABLE_COMMITTED_BY);
				createdBy = bindingSet.getValue(QUERYVARIABLE_CREATED_BY);
				users.add(userValue);
				if (revisionValue != null) {
					if (!revisionMap.containsKey(revisionValue.stringValue())
							&& !"-1".equalsIgnoreCase(revisionValue.stringValue())) {
						RevisionResponse revisionResponse = new RevisionResponse();
						revisionResponse.setRevisionNo(revisionValue.stringValue());
						revisionResponse.setComment(comment.stringValue());
						revisionResponse.setCommittedBy(committedBy.stringValue());
						revisionMap.put(revisionValue.stringValue(), revisionResponse);
					}
					if (!revisionMap.containsKey(checkoutRevisionValue.stringValue()) && null != checkoutRevisionValue
							&& !"".equalsIgnoreCase(checkoutRevisionValue.stringValue())) {
						RevisionResponse revisionResponse = new RevisionResponse();
						revisionResponse.setRevisionNo(checkoutRevisionValue.stringValue());
						revisionResponse.setComment("Revision in progress");
						revisionMap.put(checkoutRevisionValue.stringValue(), revisionResponse);
					}

				}
			}
		}
		response.setDescription(description);
		response.setLatestRevisionNumber(Integer.parseInt(latestRevisionNumber.stringValue()));
		response.setUsers(users);
		response.setCreatedBy(createdBy.stringValue());
		if (!CollectionUtils.isEmpty(revisionMap)) {
			revisions = revisionMap.values().stream().map(e -> e).collect(Collectors.toList());
		}
		response.setRevisions(revisions);
		return response;
	}

}
