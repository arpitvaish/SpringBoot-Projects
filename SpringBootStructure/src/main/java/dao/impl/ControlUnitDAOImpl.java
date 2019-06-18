package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CONTROLUNIT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_TYPE;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONTROL_UNITS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONTROL_UNITS_COMMITTED_REVISION;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.ControlUnitDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class ControlUnitDAOImpl implements ControlUnitDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void createControlUnit(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision, int lpId) {

		String controlUnitsNS = dbUtil.fetchNameSpace(CONTROLUNITS_NAMESPACE_VARIABLE, projectName);
		String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, controlUnitsNS)
		.setNamespace(GENERIC_NS_SECONDARY, loadpointNS)
		.setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter)
		.add(ROOT_SUBJECT + HAS_ID, objectCounter)
		.add(ROOT_SUBJECT + HAS_TYPE, type)
		.add(RDFS.LABEL, CONTROLUNIT_LABEL);

		builder.subject(checkoutRevision)
		.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		builder.subject(GENERIC_NS_SUB_SECONDARY + lpId)
		.add(ROOT_SUBJECT + HAS_CONTROLUNIT,GENERIC_NS_SUB + objectCounter);

		finalRepo.buildModel(builder);

	}

	@Override
	public void fetchControlUnits(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String userId = contextVariables.getUserId();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		int loadpointId = contextVariables.getLoadpontId();

		int revisionNum = contextVariables.getRevisionNumber();
		String fetchQuery = null;
		if (null != checkoutRevisionId) {
			revisionNum = Integer.parseInt(checkoutRevisionId.split("_")[1]);
			dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
			dbUtil.verifyLoadpoint(projectName, checkoutRevisionId, 0, loadpointId, userId);
			Object[] args = new Object[] { projectName, revisionNum, checkoutRevisionId, projectName, userId, loadpointId, HAS_CONTROLUNIT, revisionNum, checkoutRevisionId };
			fetchQuery = dbUtil.formatQuery(FETCH_CONTROL_UNITS_CHECKOUT_REVISION, false, args);

		} else if (revisionNum > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNum, userId);
			dbUtil.verifyLoadpoint(projectName, null, revisionNum, loadpointId, userId);
			Object[] args = new Object[] { projectName, revisionNum, projectName, userId, loadpointId, HAS_CONTROLUNIT, revisionNum };
			fetchQuery = dbUtil.formatQuery(FETCH_CONTROL_UNITS_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepo.executeQuery(fetchQuery));
	}

}
