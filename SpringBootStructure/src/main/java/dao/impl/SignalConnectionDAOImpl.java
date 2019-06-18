package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.APPLIED_TO;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.APPLIED_TO_PORT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.APPLIED_VIA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.APPLIED_VIA_PORT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_SIGNAL_CONNECTION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_SIGNAL_CONNECTIONS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_SIGNAL_CONNECTIONS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_OBJECT;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.SignalConnectionDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants;
import com.siemens.krawal.krawalcloudmanager.db.query.DBQueries;
import com.siemens.krawal.krawalcloudmanager.enums.SignalConnectionSourceType;
import com.siemens.krawal.krawalcloudmanager.enums.SignalConnectionTargetType;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class SignalConnectionDAOImpl implements SignalConnectionDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void createSignalConnection(String projectName, String crId, int objectCounter, String checkoutRevision,
			int lpId, SignalConnection connection, String userId) {

		String scNS = dbUtil.fetchNameSpace(SIGNALCONNECTIONS_NAMESPACE_VARIABLE, projectName);
		String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);

		int revNumber = Integer.parseInt(crId.split("_")[1]);

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, scNS)
		.setNamespace(GENERIC_NS_SECONDARY, loadpointNS)
		.setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter)
		.add(ROOT_SUBJECT + RelationshipConstants.HAS_ID, objectCounter)
		.add(RDFS.LABEL, SIGNALCONNECTION_LABEL);

		builder.subject(checkoutRevision)
		.add(ROOT_SUBJECT + RelationshipConstants.CREATED,GENERIC_NS_SUB + objectCounter);

		builder.subject(GENERIC_NS_SUB_SECONDARY + lpId)
		.add(ROOT_SUBJECT + HAS_SIGNAL_CONNECTION,GENERIC_NS_SUB + objectCounter);

		Object[] args = new Object[] { revNumber, crId, projectName, userId, connection.getSourceId(), revNumber, crId, SignalConnectionSourceType.valueOf(connection.getSourceType()).getSourceType() };
		String query = dbUtil.formatQuery(DBQueries.FIND_OBJECT, false, args);
		try (TupleQueryResult result1 = finalRepo.executeQuery(query)) {

			if (!result1.hasNext()) {
				throw new DBException("Source of type " + connection.getSourceType() + " with id ("
						+ connection.getSourceId() + ") is not found");
			}

			while (result1.hasNext()) {
				BindingSet bindingSet = result1.next();
				Value loadpoint = bindingSet.getValue(QUERYVARIABLE_LP);
				builder.subject(GENERIC_NS_SUB + objectCounter)
				.add(ROOT_SUBJECT + APPLIED_VIA, loadpoint)
				.add(ROOT_SUBJECT + APPLIED_VIA_PORT, connection.getSourcePort());
			}
		}
		args = new Object[] { revNumber, crId, projectName, userId, connection.getTargetId(), revNumber, crId, SignalConnectionTargetType.valueOf(connection.getTargetType()).getTargetType() };
		query = dbUtil.formatQuery(FIND_OBJECT, false, args);
		try (TupleQueryResult result1 = finalRepo.executeQuery(query)) {

			if (!result1.hasNext()) {
				throw new DBException("Target of type " + connection.getTargetType() + " with id ("
						+ connection.getTargetId() + ") is not found");
			}

			while (result1.hasNext()) {
				BindingSet bindingSet = result1.next();
				Value loadpoint = bindingSet.getValue(QUERYVARIABLE_LP);
				builder.subject(GENERIC_NS_SUB + objectCounter)
				.add(ROOT_SUBJECT + APPLIED_TO, loadpoint)
				.add(ROOT_SUBJECT + APPLIED_TO_PORT, connection.getTargetPort());
			}
		}
		finalRepo.buildModel(builder);

	}

	@Override
	public void fetchSignalConnections(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		int loadpointId = contextVariables.getLoadpontId();
		String userId = contextVariables.getUserId();

		int revisionNumber = contextVariables.getRevisionNumber();
		String fetchQuery = null;
		if (null != checkoutRevisionId) {
			revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
			dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
			dbUtil.verifyLoadpoint(projectName, checkoutRevisionId, 0, loadpointId, userId);
			Object[] args = new Object[] { projectName, revisionNumber, checkoutRevisionId, projectName, userId,
					loadpointId, HAS_SIGNAL_CONNECTION, revisionNumber, checkoutRevisionId, revisionNumber, checkoutRevisionId, };
			fetchQuery = dbUtil.formatQuery(FETCH_SIGNAL_CONNECTIONS_CHECKOUT_REVISION, false, args);

		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			dbUtil.verifyLoadpoint(projectName, null, revisionNumber, loadpointId, userId);
			Object[] args = new Object[] { projectName, revisionNumber, projectName, userId, loadpointId,
					HAS_SIGNAL_CONNECTION, revisionNumber, revisionNumber };
			fetchQuery = dbUtil.formatQuery(FETCH_SIGNAL_CONNECTIONS_COMMITTED_REVISION, false, args);
		}
		contextVariables.setResult(finalRepo.executeQuery(fetchQuery));
	}

}
