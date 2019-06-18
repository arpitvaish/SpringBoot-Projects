package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_USER;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.DELETE_RELATION_PROJECT;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.DELETE_REVISIONS_PROJECT;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.VERIFY_PROJECT_TUPLE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.dao.UserDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants;
import com.siemens.krawal.krawalcloudmanager.db.query.DBQueries;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.User;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class UserDAOImpl implements UserDAO, DBConstants {

	@Autowired
	private FinalRepositories finalRepo;

	@Autowired
	private DBUtil dbUtil;

	@Override
	public void createUser(String projectId, List<User> users, Map<String, Value> returnedUsers) {

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(USER, USER_NAMESPACE).setNamespace(RDF.NS);
		Iterator<User> iterator = users.iterator();
		while (iterator.hasNext()) {
			User user = iterator.next();
			if (user.getGid() != null && user.getUserName() != null) {
				String userId = user.getGid().toUpperCase();
				Value userValue = returnedUsers.get(userId);
				if (userValue != null) {
					builder.subject(ROOT_SUBJECT + projectId).add(ROOT_SUBJECT + HAS_USER, userValue);
				} else {
					builder.subject(USER_ID + userId).add(USER_ID + RelationshipConstants.HAS_GID, userId);
					builder.subject(USER_ID + userId).add(USER_ID + RelationshipConstants.HAS_USERNAME,
							user.getUserName());

					builder.subject(ROOT_NAMESPACE + projectId).add(ROOT_NAMESPACE + HAS_USER,
							finalRepo.getValueFactory().createIRI("", USER_NAMESPACE + userId));
				}
			} else {
				throw new RequestNotCompleteException(
						"Required parameters are not present in the request - Gid (or) UserName.");
			}
		}
		finalRepo.buildModel(builder);
	}

	@Override
	public void removeUserRelationshipWithProject(String projectId, String userId) {
		String revision = fetchCheckoutRevision(projectId, userId);
		if (revision != null) {
			Object[] args = new Object[] { projectId, projectId, userId, projectId, revision, revision, revision,
					projectId, userId, projectId, revision, revision, revision };
			String query = dbUtil.formatQuery(DELETE_REVISIONS_PROJECT, false, args);
			finalRepo.updateQuery(query);
		} else {
			Object[] args = new Object[] { projectId, userId, projectId, userId };
			String query = dbUtil.formatQuery(DELETE_RELATION_PROJECT, false, args);
			finalRepo.updateQuery(query);
		}

	}

	public String fetchCheckoutRevision(String projectId, String userId) {
		String revision = null;
		Object[] args = new Object[] { projectId, userId };
		String query = dbUtil.formatQuery(DBQueries.FETCH_USERS_PROJECT_REVISION, true, args);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value revisionValue = bindingSet.getValue("revision");
				Value checkoutRevisionId = bindingSet.getValue("checkoutRevisionId");
				String checkoutRevision = checkoutRevisionId.stringValue();
				if (userId.equals(checkoutRevision.split("_")[0])) {
					String revisionString = revisionValue.stringValue();
					revision = revisionString.split("/")[7];
				}
			}
		}
		return revision;
	}

	public void removeUserInfoNoOtherProjects(String projectId, String userId) {
		String query = dbUtil.formatQuery(DBQueries.FETCH_USER_ACCESS_ALL_PROJECTS, false, userId);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			if (!result.hasNext()) {
				Object[] args = new Object[] { userId, userId };
				query = dbUtil.formatQuery(DBQueries.DELETE_USER, false, args);
				finalRepo.updateQuery(query);
			}
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				bindingSet.getValue("projects");
			}
		}
	}

	public Map<String, Value> fetchUsersAssociatedWithProject(String projectId, Set<String> userIds) {
		Map<String, Value> responseMap = new HashMap<>();
		String query = dbUtil.formatQuery(DBQueries.FETCH_PROJECT_USERS, true, projectId, userIds);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			if (!result.hasNext()) {
				throw new DBException("Project name or UserId not found.");
			}
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value userValue = bindingSet.getValue(DBConstants.QUERYVARIABLE_USER);
				Value userIdValue = bindingSet.getValue("id");
				responseMap.put(userIdValue.stringValue(), userValue);
			}
		}
		return responseMap;
	}

	@Override
	public boolean verifyprojectExists(String projectName) {

		String query = dbUtil.formatQuery(VERIFY_PROJECT_TUPLE, false, projectName);
		return finalRepo.booleanQuery(query);
	}
}
