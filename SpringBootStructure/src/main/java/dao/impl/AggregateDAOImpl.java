package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.DELETED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ASSEMBLY_ASSOCIATION;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_PARTS;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_TYPE;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_AGGREGATES_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_AGGREGATES_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_AGGREGATE_ASSOCIATION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_ASSEMBLY_PARTS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_ASSEMBLY_PARTS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_AGGREGATES;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.AggregateDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class AggregateDAOImpl implements AggregateDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepositories;

	public void createAggregate(String projectName, String crId, int objectCounter, String type,
			String checkoutRevision) {

		String aggregateNamespace = dbUtil.fetchNameSpace(AGGREGATES_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, aggregateNamespace)
		.setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter)
		.add(ROOT_SUBJECT + HAS_ID, objectCounter)
		.add(ROOT_SUBJECT + HAS_TYPE, type)
		.add(RDFS.LABEL, AGGREGATE_LABEL);

		builder.subject(checkoutRevision)
		.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		finalRepositories.buildModel(builder);
	}

	/**
	 * 
	 * @param contextVariables
	 */
	public void fetchAggregates(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();
		String fetchAggregatesQuery = null;
		int revisionNumber = contextVariables.getRevisionNumber();

		if (null != checkoutRevisionId) {
			dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
			revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
			Object[] args = new Object[] { revisionNumber, checkoutRevisionId, projectName, userId, revisionNumber, checkoutRevisionId };
			fetchAggregatesQuery = dbUtil.formatQuery(FETCH_AGGREGATES_CHECKOUT_REVISION, false, args);
		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			Object[] args = new Object[] { revisionNumber, projectName, userId, revisionNumber };
			fetchAggregatesQuery = dbUtil.formatQuery(FETCH_AGGREGATES_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepositories.executeQuery(fetchAggregatesQuery));

	}

	/**
	 * 
	 * @param contextVariables
	 */
	public void assemble(ContextVariables contextVariables) {
		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		int objectId = contextVariables.getObjectId();
		int revisionNumber = Integer.parseInt(crId.split("_")[1]);
		String userId = contextVariables.getUserId();
		String checkoutRevision = contextVariables.getCheckoutRevisionIRI();

		Object[] args = new Object[] { revisionNumber, crId, projectName, userId, objectId, revisionNumber, crId, revisionNumber, crId };
		String query = dbUtil.formatQuery(FETCH_AGGREGATE_ASSOCIATION, false, args);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			if (null != result) {
				if (!result.hasNext()) {
					throw new DBException("Aggregate with id: " + objectId + " is not found.");
				}
				while (result.hasNext()) {

					BindingSet bindingSet = result.next();
					Value parentAggregateIRI = bindingSet.getValue("objects");
					Value aggregateAssociationIRI = bindingSet.getValue("d");
					setAssemblyAggregates(contextVariables, parentAggregateIRI, aggregateAssociationIRI,
							checkoutRevision, userId);
				}
			}
		}
	}

	/**
	 * if aggregate is already associated with parts, it will be overwritten
	 * behavior is similar to set data methods
	 * 
	 * @param contextVariables
	 * @param parentAggregateIRI
	 * @param aggregateAssociationIRI
	 * @param checkoutRevision
	 * @param conn
	 */
	private void setAssemblyAggregates(ContextVariables contextVariables, Value parentAggregateIRI,
			Value aggregateAssociationIRI, String checkoutRevision, String userId) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		int revisionNumber = Integer.parseInt(crId.split("_")[1]);

		String aggregateNS = dbUtil.fetchNameSpace(AGGREGATES_NAMESPACE_VARIABLE, projectName);
		String aggregateAssocNS = dbUtil.fetchNameSpace(AGGREGATES_ASSOCIATION_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, aggregateNS)
		.setNamespace(GENERIC_NS_SECONDARY, aggregateAssocNS)
		.setNamespace(RDFS.NS);

		if (null != aggregateAssociationIRI) {
			builder.subject(checkoutRevision)
			.add(ROOT_SUBJECT + DELETED, aggregateAssociationIRI);
		}

		// find new aggregates
		Map<Integer, Value> responseMap = findAggregates(contextVariables, revisionNumber, userId);

		if (!responseMap.isEmpty()) {
			String uuid = UUID.randomUUID().toString();

			builder.subject(GENERIC_NS_SUB_SECONDARY + uuid)
			.add(ROOT_SUBJECT + HAS_ID, uuid)
			.add(RDFS.LABEL,AGGREGATE_ASSEMBLY_LABEL);

			builder.subject(parentAggregateIRI.stringValue())
			.add(ROOT_SUBJECT + HAS_ASSEMBLY_ASSOCIATION,GENERIC_NS_SUB_SECONDARY + uuid);

			builder.subject(checkoutRevision)
			.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB_SECONDARY + uuid);

			for (Integer aggregateId : contextVariables.getMembers()) {
				if (responseMap.containsKey(aggregateId)) {
					builder.subject(GENERIC_NS_SUB_SECONDARY + uuid)
					.add(ROOT_SUBJECT + HAS_PARTS,responseMap.get(aggregateId));
				} else {
					throw new DBException("Aggregate id (" + aggregateId + ") is not found!!");
				}
			}
		}
		else {
			throw new DBException("Assembly parts are not found!");
		}

		finalRepositories.buildModel(builder);
	}

	private Map<Integer, Value> findAggregates(ContextVariables contextVariables, int revisionNumber, String userId) {
		Map<Integer, Value> responseMap = new HashMap<>();
		List<Integer> aggregateIds = contextVariables.getMembers();
		
		Object[] args = new Object[] { revisionNumber, contextVariables.getCheckoutRevisionId(),
				contextVariables.getProjectName(), userId, revisionNumber,
				contextVariables.getCheckoutRevisionId(), aggregateIds, AGGREGATE_LABEL };
		
		String query = dbUtil.formatQuery(FIND_AGGREGATES, false, args);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value aggregateIRI = bindingSet.getValue(QUERYVARIABLE_AGGREGATE);
				Value id = bindingSet.getValue(QUERYVARIABLE_ID);
				int aggregateId = Integer.parseInt(id.stringValue());
				responseMap.put(aggregateId, aggregateIRI);
			}
		}
		return responseMap;

	}

	/**
	 * 
	 */
	public ContextVariables getAssemblyParts(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		int objectId = contextVariables.getObjectId();
		String userId = contextVariables.getUserId();

		String fetchPartsQuery = null;
		int revisionNumber = contextVariables.getRevisionNumber();

		if (null != crId) {
			dbUtil.verifyRevision(projectName, crId, 0, userId);
			revisionNumber = Integer.parseInt(crId.split("_")[1]);
			Object[] args = new Object[] { revisionNumber, crId, projectName, userId, objectId,
					revisionNumber, crId, revisionNumber, crId };
			fetchPartsQuery = dbUtil.formatQuery(FETCH_ASSEMBLY_PARTS_CHECKOUT_REVISION, false, args);
		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			Object[] args = new Object[] { revisionNumber, projectName, userId, objectId,
					revisionNumber, revisionNumber };
			fetchPartsQuery = dbUtil.formatQuery(FETCH_ASSEMBLY_PARTS_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepositories.executeQuery(fetchPartsQuery));

		return contextVariables;
	}
}
