package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_LOADPOINTS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_LOADPOINTS_COMMITTED_REVISION;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadpointDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class LoadpointDAOImpl implements LoadpointDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void createLoadpoint(String projectName, String crId, int objectCounter, String checkoutRevision) {

		String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, loadpointNS)
		.setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter)
		.add(ROOT_SUBJECT + HAS_ID, objectCounter)
		.add(RDFS.LABEL,LOADPOINT_LABEL);

		builder.subject(checkoutRevision)
		.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		finalRepo.buildModel(builder);
	}

	@Override
	public void fetchLoadpoints(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		int revisionNumber = contextVariables.getRevisionNumber();
		String query = null;

		if (null != crId) {
			revisionNumber = Integer.parseInt(crId.split("_")[1]);
			dbUtil.verifyRevision(projectName, crId, 0, userId);
			Object[] args = new Object[] { revisionNumber, crId, projectName, userId, revisionNumber, crId };
			query = dbUtil.formatQuery(FETCH_LOADPOINTS_CHECKOUT_REVISION, false, args);

		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			Object[] args = new Object[] { revisionNumber, projectName, userId, revisionNumber };
			query = dbUtil.formatQuery(FETCH_LOADPOINTS_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepo.executeQuery(query));

	}

}
