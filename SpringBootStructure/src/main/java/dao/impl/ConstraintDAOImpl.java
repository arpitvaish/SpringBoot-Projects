package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CONSTRAINT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_TYPE;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.REFERENCING_TO;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONSTRAINTS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONSTRAINTS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_OBJECT;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.ConstraintDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class ConstraintDAOImpl implements ConstraintDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepositories;

	@Override
	public void createConstraint(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision, int lpId, String userId) {

		String constraintNS = dbUtil.fetchNameSpace(CONSTRAINTS_NAMESPACE_VARIABLE, projectName);
		String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);
		

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(GENERIC_NS, constraintNS)
				.setNamespace(GENERIC_NS_SECONDARY, loadpointNS).setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter).add(ROOT_SUBJECT + HAS_ID, objectCounter)
				.add(ROOT_SUBJECT + HAS_TYPE, type).add(RDFS.LABEL, CONSTRAINT_LABEL);

		builder.subject(checkoutRevision).add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		builder.subject(GENERIC_NS_SUB_SECONDARY + lpId).add(ROOT_SUBJECT + HAS_CONSTRAINT,
				GENERIC_NS_SUB + objectCounter);

		finalRepositories.buildModel(builder);

	}
	
	public void createReferenceToLpConstraint(ContextVariables contextVariables, int referenceLP, int objectCounter){
		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();
		int revNumber = Integer.parseInt(crId.split("_")[1]);
		
		ModelBuilder builder = new ModelBuilder();
		
		Object[] args = new Object[] { revNumber, crId, projectName, userId, referenceLP, revNumber, crId, LOADPOINT_LABEL };
		String query = dbUtil.formatQuery(FIND_OBJECT, false, args);
		try (TupleQueryResult queryResult = finalRepositories.executeQuery(query)) {
			if (!queryResult.hasNext()) {
				throw new DBException("Referenced Loadpoint (" + referenceLP + ") is not found");
			}
			while (queryResult.hasNext()) {

				BindingSet bindingSet = queryResult.next();
				bindingSet.getValue(QUERYVARIABLE_LP);
				
				String constraintNS = dbUtil.fetchNameSpace(CONSTRAINTS_NAMESPACE_VARIABLE, projectName);				
				String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);

				builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(GENERIC_NS, constraintNS)
				.setNamespace(GENERIC_NS_SECONDARY, loadpointNS).setNamespace(RDFS.NS);
				
				builder.subject(GENERIC_NS_SUB + objectCounter).add(ROOT_SUBJECT + REFERENCING_TO,
						GENERIC_NS_SUB_SECONDARY + referenceLP);
			}
		}
		finalRepositories.buildModel(builder);
	}

	@Override
	public void fetchConstraints(ContextVariables contextVariables) {

		String crId = contextVariables.getCheckoutRevisionId();
		String projectName = contextVariables.getProjectName();
		int lpId = contextVariables.getLoadpontId();
		String uId = contextVariables.getUserId();

		int revisionNo = contextVariables.getRevisionNumber();
		String fetchQuery = null;

		if (null != crId) {
			revisionNo = Integer.parseInt(crId.split("_")[1]);
			dbUtil.verifyRevision(projectName, crId, 0, uId);
			dbUtil.verifyLoadpoint(projectName, crId, 0, lpId, uId);
			Object[] args = new Object[] { projectName, revisionNo, crId, projectName, uId, lpId, HAS_CONSTRAINT, revisionNo, crId, revisionNo, crId };
			fetchQuery = dbUtil.formatQuery(FETCH_CONSTRAINTS_CHECKOUT_REVISION, false, args);

		} else if (revisionNo > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNo, uId);
			dbUtil.verifyLoadpoint(projectName, null, revisionNo, lpId, uId);
			Object[] args = new Object[] { projectName, revisionNo, projectName, uId, lpId, HAS_CONSTRAINT, revisionNo, revisionNo };
			fetchQuery = dbUtil.formatQuery(FETCH_CONSTRAINTS_COMMITTED_REVISION, false, args);
		}
		contextVariables.setResult(finalRepositories.executeQuery(fetchQuery));
	}
}
