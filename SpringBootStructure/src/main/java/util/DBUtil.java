package com.siemens.krawal.krawalcloudmanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.dao.impl.FinalRepositories;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.query.DBQueries;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.ValidationException;

@Component
public class DBUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);

	@Autowired
	private FinalRepositories finalRepositories;

	public String verifyRevision(String projectName, String checkoutRevisionId, int revisionNo, String userId) {

		String checkoutRevision = null;
		String query = returnQuery(projectName, checkoutRevisionId, revisionNo, userId);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			if (!result.hasNext()) {
				throw new ValidationException("Please verify project/checkout revision!!");
			}
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				checkoutRevision = bindingSet.getValue("revision").stringValue();
			}
		}
		return checkoutRevision;

	}

	private String returnQuery(String projectName, String checkoutRevisionId, int revisionNo, String userId) {

		String query = null;
		if (revisionNo > 0) {
			Object[] args = new Object[] { projectName, userId, revisionNo};
			query = formatQuery(DBQueries.VERIFY_COMMITTED_REVISION, false, args);
		} else {
			Object[] args = new String[] { projectName, userId, checkoutRevisionId};
			query = formatQuery(DBQueries.VERIFY_CHECKOUT_REVISION, false, args);
		}
		return query;
	}

	public void verifyLoadpoint(String projectName, String checkoutRevisionId, int revisionNumber, int lpId,
			String userId) {
		int revNumber = revisionNumber;
		String crId = checkoutRevisionId;
		if (revisionNumber > 0) {
			crId = "";
		} else {
			revNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
		}
		Object[] args = new Object[] { revNumber, crId, projectName, userId, lpId, revisionNumber, crId,"Loadpoint" };
		String query = formatQuery(DBQueries.FIND_OBJECT, false, args);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			if (!result.hasNext()) {
				throw new ValidationException("Loadpoint (" + lpId + ") is not found");
			}
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				bindingSet.getBindingNames();
			}

		}
	}

	public Map<String, Value> fetchUsers(Set<String> userIds) {
		Map<String, Value> responseMap = new HashMap<>();
		String query = formatQuery(DBQueries.FETCH_USERS, true, userIds);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value userValue = bindingSet.getValue(DBConstants.QUERYVARIABLE_USER);
				Value userIdValue = bindingSet.getValue("id");
				responseMap.put(userIdValue.stringValue(), userValue);
			}

		}
		return responseMap;
	}

	/**
	 * Query Resolver
	 * 
	 * @param query
	 * @param isString
	 * @param args
	 * @return
	 */
	public String formatQuery(String query, boolean isString, Object... args) {
		try {
			String queryResult = String.format(query, args);
			if (isString) {

				queryResult = queryResult.replace("[", "\"").replace(", ", "\",\"").replace("]", "\"");
			} else {
				queryResult = queryResult.replace("[", "");
				queryResult = queryResult.replace("]", "");
			}
			LOGGER.info("=== Query executing is ===\n\n: {}", queryResult, "\n");
			return queryResult;
		} catch (Exception e) {
			throw new ValidationException("Error framing SPARQL query");
		}
	}

	/**
	 * 
	 * @param value
	 * @param projectName
	 * @return
	 */
	public String fetchNameSpace(String value, String projectName) {
		return DBConstants.ROOT_NAMESPACE + projectName + value + DBConstants.NAMESPACE_SLASH;
	}

	public String capitalizeString(String txt) {

		if (txt.equalsIgnoreCase(ObjectType.AGGREGATE.toString())) {
			return ObjectType.AGGREGATE.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.CYCLESEGMENT.toString())) {
			return ObjectType.CYCLESEGMENT.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.MEDIUMCONNECTION.toString())) {
			return ObjectType.MEDIUMCONNECTION.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.LOADPOINT.toString())) {
			return ObjectType.LOADPOINT.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.CONSTRAINT.toString())) {
			return ObjectType.CONSTRAINT.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.SIGNALCONNECTION.toString())) {
			return ObjectType.SIGNALCONNECTION.getObjectType();
		} else if (txt.equalsIgnoreCase(ObjectType.CONTROLUNIT.toString())) {
			return ObjectType.CONTROLUNIT.getObjectType();
		}
		return txt;
	}
}
