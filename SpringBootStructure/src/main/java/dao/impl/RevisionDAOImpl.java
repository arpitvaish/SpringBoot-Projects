package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CHECKOUT_REVISION_ID;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.*;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_REVISION_NUMBER;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.COMMIT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.DELETE_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.DELETE_NODES;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_PROJECT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.VERIFY_CR_PROJECT_USER_ACCESS;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.VERIFY_USER_ACCESS_PROJECT_REVISION;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.dao.RevisionDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.model.CheckoutRevisionResponse;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class RevisionDAOImpl implements RevisionDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public CheckoutRevisionResponse checkoutRevisionExists(String projectName, String userId, String crId) {

		CheckoutRevisionResponse response = new CheckoutRevisionResponse();
		Object[] args = new String[] { projectName, projectName, crId, userId };
		String query = dbUtil.formatQuery(VERIFY_CR_PROJECT_USER_ACCESS, false, args);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				response.setCheckoutRevision(bindingSet.getValue("revisions"));
				response.setLatestRevisionNumber(bindingSet.getValue("latestRevisionNumber"));
				response.setUser(bindingSet.getValue("user"));
			}
		}

		return response;

	}

	@Override
	public void createCheckoutRevision(String projectName, String userId, String checkoutRevisionId) {
		String revisionNS = dbUtil.fetchNameSpace(REVISIONS_NAMESPACE_VARIABLE, projectName);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(REV, revisionNS).setNamespace(USER, USER_NAMESPACE)
				.setNamespace(RDF.NS);
		String uuid = UUID.randomUUID().toString();

		builder.subject(REV_SUBJECT + uuid).add(ROOT_SUBJECT + HAS_CHECKOUT_REVISION_ID, checkoutRevisionId)
				.add(ROOT_SUBJECT + HAS_REVISION_NUMBER, -1);

		builder.subject(ROOT_SUBJECT + projectName).add(ROOT_SUBJECT + HAS_REVISION, REV_SUBJECT + uuid);
		finalRepo.buildModel(builder);

	}

	@Override
	public CheckoutRevisionResponse verifyUserAccessCheckoutRevision(String projectName, String userId, String crId) {
		CheckoutRevisionResponse response = new CheckoutRevisionResponse();
		Object[] args = new String[] { projectName, userId, crId };
		String query = dbUtil.formatQuery(VERIFY_USER_ACCESS_PROJECT_REVISION, false, args);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				response.setUser(bindingSet.getValue("user"));
				response.setCheckoutRevision(bindingSet.getValue("revision"));
			}
		}
		return response;
	}

	@Override
	public void deleteCheckoutRevision(String projectName, String crId, String userId) {

		Object[] args = new String[] { projectName, crId };
		String query = dbUtil.formatQuery(DELETE_CHECKOUT_REVISION, false, args);
		finalRepo.updateQuery(query);
	}

	public void deleteTuples(String projectName, String crId) {
		Object[] args = new String[] { projectName, crId };
		String query = dbUtil.formatQuery(DELETE_NODES, false, args);
		finalRepo.updateQuery(query);

	}

	/**
	 * commit revision is synchronized
	 * 
	 * @param revNumber
	 * @param projectName
	 * @param checkoutRevisionId
	 * @param contextVariables
	 */
	@Override
	public synchronized void synchronizedCommit(int revNumber, String projectName, Value checkoutRevision,
			String checkoutRevisionId) {

		Object[] args = new String[] { projectName };
		String query = dbUtil.formatQuery(FIND_PROJECT_REVISION, false, args);
		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value latestRevisionNumber = bindingSet.getValue("revNum");
				int revisionNumber = Integer.parseInt(latestRevisionNumber.stringValue());
				int commitRevisionNo = revisionNumber + 1;
				String[] arr = checkoutRevision.stringValue().split("/");
				String revisionId = arr[arr.length - 1];
				if (revNumber < revisionNumber) {
					throw new DBException("Please merge your changes with latest revision: ("
							+ latestRevisionNumber.stringValue() + ")");
				} else if (revNumber == revisionNumber) {
					args = new Object[] { projectName, projectName, revisionNumber, revisionId, checkoutRevisionId, -1,
							projectName, commitRevisionNo, revisionId, commitRevisionNo, projectName, projectName,
							revisionId, revisionNumber, revisionId, checkoutRevisionId, -1 };
					String updateQuery = dbUtil.formatQuery(COMMIT_REVISION, false, args);
					finalRepo.updateQuery(updateQuery);
				}
			}
		}
	}

	@Override
	public void updateCommittedRevision(String projectName, Value checkoutRevision, String comment, String userId) {
		String revisionNS = dbUtil.fetchNameSpace(REVISIONS_NAMESPACE_VARIABLE, projectName);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(REV, revisionNS).setNamespace(RDF.NS);
		builder.subject(checkoutRevision.stringValue()).add(ROOT_SUBJECT + HAS_COMMENT, comment)
				.add(ROOT_SUBJECT + COMMITTED_BY, userId);

		finalRepo.buildModel(builder);
	}

	@Override
	public Map<String, Value> fetchUsers(Set<String> users) {

		return dbUtil.fetchUsers(users);

	}

}
