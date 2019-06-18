package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.BELONGS_TO;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.DELETED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_DESIGN_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_LOAD_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_PLANT_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_UUID;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_DATA_OBJECT_ID;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_LP_DATA_OBJECT_ID;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.DataParametersDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.query.DBQueries;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class DataParametersDAOImpl implements DataParametersDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void setPlantParameters(ContextVariables contextVariables, Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String dataNS = dbUtil.fetchNameSpace(PLANT_DATA_NAMESPACE_VARIABLE, projectName);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, dataNS)
		.setNamespace(RDFS.NS);
		setData(builder, contextVariables, HAS_PLANT_DATA, DBConstants.PLANTDATA_LABEL, data);

	}

	@Override
	public void setLoadParameters(ContextVariables contextVariables, Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String dataNS = dbUtil.fetchNameSpace(LOAD_DATA_NAMESPACE_VARIABLE, projectName);
		String loadpointNS = dbUtil.fetchNameSpace(LOADPOINTS_NAMESPACE_VARIABLE, projectName);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, dataNS)
		.setNamespace(RDFS.NS)
		.setNamespace(LOADPOINT_NS, loadpointNS);
		setData(builder, contextVariables, HAS_LOAD_DATA, DBConstants.LOADDATA_LABEL, data);
	}

	@Override
	public void setDesignParameters(ContextVariables contextVariables, Map<String, String> data) {
		String projectName = contextVariables.getProjectName();
		String dataNS = dbUtil.fetchNameSpace(DESIGN_DATA_NAMESPACE_VARIABLE, projectName);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, dataNS)
		.setNamespace(RDFS.NS);
		setData(builder, contextVariables, HAS_DESIGN_DATA, DBConstants.DESIGNDATA_LABEL, data);
	}

	/**
	 * 
	 * @param builder
	 * @param contextVariables
	 * @param relation
	 * @param label
	 */
	private void setData(ModelBuilder builder, ContextVariables contextVariables, String relation, String label,
			Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		String objectType = dbUtil.capitalizeString(contextVariables.getObjectType());
		int objectId = contextVariables.getObjectId();
		int lpId = contextVariables.getLoadpontId();
		String userId = contextVariables.getUserId();
		int revNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
		String crIRI = contextVariables.getCheckoutRevisionIRI();
		String revNS = dbUtil.fetchNameSpace(REVISIONS_NAMESPACE_VARIABLE, projectName);

		builder.setNamespace(GENERIC_NS_SECONDARY, revNS);

		String query = null;
		if (lpId > 0) {
			Object[] args = new Object[] { projectName, revNumber, checkoutRevisionId, projectName, userId,
					objectId, objectType, lpId, revNumber, checkoutRevisionId, revNumber, checkoutRevisionId };
			query = dbUtil.formatQuery(FIND_LP_DATA_OBJECT_ID, false, args);

		} else {
			Object[] args = new Object[] { revNumber, checkoutRevisionId, projectName, userId, objectId,
					objectType, relation, revNumber, checkoutRevisionId, revNumber, checkoutRevisionId };
			query = dbUtil.formatQuery(FIND_DATA_OBJECT_ID, false, args);
		}

		try (TupleQueryResult result = finalRepo.executeQuery(query)) {

			if (!result.hasNext()) {
				throw new DBException("Object with id (" + objectId + ") is not found to associate.");
			}
			String uuid = UUID.randomUUID().toString();

			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value objectValue = bindingSet.getValue("object");
				Value existingDataValue = bindingSet.getValue("data");

				builder.subject(objectValue.stringValue())
				.add(ROOT_SUBJECT + relation, GENERIC_NS_SUB + uuid);

				builder.subject(DBConstants.GENERIC_NS_SUB + uuid)
				.add(ROOT_SUBJECT + HAS_UUID, uuid)
				.add(RDFS.LABEL, label);

				builder.subject(crIRI)
				.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + uuid);

				if (null != existingDataValue) {
					builder.subject(crIRI).add(ROOT_SUBJECT + DELETED, existingDataValue);
				}

				if (lpId > 0) {
					builder.subject(LOADPOINT_NS_SUB + lpId)
					.add(ROOT_SUBJECT + BELONGS_TO, GENERIC_NS_SUB + uuid);
				}

			}

			String subject = DBConstants.GENERIC_NS_SUB + uuid;
			mapData(builder, data, subject);
			finalRepo.buildModel(builder);

		}
	}

	/**
	 * 
	 * @param builder
	 * @param data
	 * @param subject
	 */
	private void mapData(ModelBuilder builder, Map<String, String> data, String subject) {

		if (!CollectionUtils.isEmpty(data)) {
			for (Entry<String, String> map : data.entrySet()) {
				String predicate = map.getKey().replaceAll(" ", "%20");
				String object = map.getValue();
				builder.subject(subject).add(DBConstants.ROOT_SUBJECT + predicate, object);
			}
		}
	}

	public void fetchData(ContextVariables contextVariables, String relation, String label) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		int objectId = contextVariables.getObjectId();
		String objectType = dbUtil.capitalizeString(contextVariables.getObjectType());
		int lpId = contextVariables.getLoadpontId();
		String userId = contextVariables.getUserId();

		String query = null;
		int revisionNumber = contextVariables.getRevisionNumber();

		if (lpId > 0) {

			if (null != checkoutRevisionId) {
				dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
				dbUtil.verifyLoadpoint(projectName, checkoutRevisionId, 0, lpId, userId);
				revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
				Object[] args = new Object[] { projectName, revisionNumber, checkoutRevisionId, projectName, userId,
						objectId, objectType, lpId, label, revisionNumber, checkoutRevisionId, revisionNumber, checkoutRevisionId };
				query = dbUtil.formatQuery(DBQueries.FIND_LP_DATA_CHECKOUT_REVISION, false, args);
			} else if (revisionNumber > 0) {

				dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
				dbUtil.verifyLoadpoint(projectName, null, revisionNumber, lpId, userId);
				Object[] args = new Object[] { projectName, revisionNumber, projectName, userId, objectId, objectType,
						lpId, label, revisionNumber, revisionNumber };
				query = dbUtil.formatQuery(DBQueries.FIND_LP_DATA_COMMITTED_REVISION, false, args);
			}
		} else {
			if (null != checkoutRevisionId) {
				dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
				revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
				Object[] args = new Object[] { revisionNumber, checkoutRevisionId, projectName, userId, objectId,
						relation, objectType, label, revisionNumber, checkoutRevisionId, revisionNumber, checkoutRevisionId };
				query = dbUtil.formatQuery(DBQueries.FIND_DATA_CHECKOUT_REVISION, false, args);
			} else if (revisionNumber > 0) {

				dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
				Object[] args = new Object[] { revisionNumber, projectName, userId, objectId, relation, objectType,
						label, revisionNumber, revisionNumber };
				query = dbUtil.formatQuery(DBQueries.FIND_DATA_COMMITTED_REVISION, false, args);
			}

		}

		contextVariables.setResult(finalRepo.executeQuery(query));
	}
}
