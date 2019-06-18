package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CONTAINS;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.DELETED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CYCLE_SEGMENT_ASSOCIATION;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION_ID;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CYCLE_SEGMENT_COMMITTED_REVISION;
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
import com.siemens.krawal.krawalcloudmanager.dao.CycleSegmentDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class CycleSegmentDAOImpl implements CycleSegmentDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void createCycleSegment(String projectName, String crId, int objectCounter, List<Integer> aggregateIds,
			String checkoutRevision, String userId) {

		String csNameSpace = dbUtil.fetchNameSpace(CYCLESEGMENTS_NAMESPACE_VARIABLE, projectName);
		String csaNameSpace = dbUtil.fetchNameSpace(CYCLESEGMENTS_ASSOCIATION_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(GENERIC_NS, csNameSpace)
				.setNamespace(GENERIC_NS_SECONDARY, csaNameSpace).setNamespace(RDFS.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter).add(ROOT_SUBJECT + HAS_ID, objectCounter).add(RDFS.LABEL,
				CYCLE_SEGMENT_LABEL);

		builder.subject(checkoutRevision).add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		Map<Integer, Value> responseMap = fetchAggregatesForCS(projectName, crId, aggregateIds, userId);

		mapAggregatesToCS(builder, aggregateIds, objectCounter, checkoutRevision, responseMap);

		finalRepo.buildModel(builder);
	}

	/**
	 * 
	 * @param projectName
	 * @param crId
	 * @param objectCounter
	 * @param aggregateIds
	 * @param checkoutRevision
	 * @param userId
	 * @return
	 */
	private Map<Integer, Value> fetchAggregatesForCS(String projectName, String crId, List<Integer> aggregateIds,
			String userId) {
		int revNumber = Integer.parseInt(crId.split("_")[1]);

		Object[] args = new Object[] { revNumber, crId, projectName, userId, revNumber, crId, aggregateIds, AGGREGATE_LABEL };
		String query = dbUtil.formatQuery(FIND_AGGREGATES, false, args);

		Map<Integer, Value> responseMap = new HashMap<>();

		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
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

	@Override
	public void fetchCycleSegments(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String userId = contextVariables.getUserId();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		String fetchQuery = null;
		int revisionNumber = contextVariables.getRevisionNumber();
		if (null != checkoutRevisionId) {

			dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);

			revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
			Object[] args = new Object[] { revisionNumber, checkoutRevisionId, projectName, userId, revisionNumber, checkoutRevisionId, revisionNumber, checkoutRevisionId };
			fetchQuery = dbUtil.formatQuery(FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION, false, args);

		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);

			Object[] args = new Object[] { revisionNumber, projectName, userId, revisionNumber, revisionNumber };
			fetchQuery = dbUtil.formatQuery(FETCH_CYCLE_SEGMENT_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepo.executeQuery(fetchQuery));

	}

	/**
	 * 
	 * @param builder
	 * @param aggregateIds
	 * @param contextVariables
	 * @param responseMap
	 */
	private void mapAggregatesToCS(ModelBuilder builder, List<Integer> aggregateIds, int counter,
			String checkoutRevision, Map<Integer, Value> responseMap) {

		String uuid = UUID.randomUUID().toString();

		builder.subject(GENERIC_NS_SUB_SECONDARY + uuid).add(ROOT_SUBJECT + HAS_ID, uuid).add(RDFS.LABEL,
				CYCLE_SEGMENT_ASSOCIATION_LABEL);

		builder.subject(GENERIC_NS_SUB + counter).add(ROOT_SUBJECT + HAS_CYCLE_SEGMENT_ASSOCIATION,
				GENERIC_NS_SUB_SECONDARY + uuid);

		builder.subject(checkoutRevision).add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + counter)
				.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB_SECONDARY + uuid);

		for (Integer aggregateId : aggregateIds) {
			if (responseMap.containsKey(aggregateId)) {
				builder.subject(GENERIC_NS_SUB_SECONDARY + uuid).add(ROOT_SUBJECT + CONTAINS,
						responseMap.get(aggregateId));
			} else {
				throw new DBException("Aggregate id (" + aggregateId + ") is not found!!");
			}
		}

	}

	/**
	 * update cycle segment
	 * 
	 * @param contextVariables
	 */
	public void joinCycleSegment(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		int objectId = contextVariables.getObjectId();
		String userId = contextVariables.getUserId();

		String checkoutRevision = dbUtil.verifyRevision(projectName, crId, 0, userId);

		// fetch Cycle Segment
		int revisionNumber = Integer.parseInt(crId.split("_")[1]);
		
		Object[] args = new Object[] { revisionNumber, crId, projectName, userId, objectId,
				revisionNumber, crId, revisionNumber, crId, ObjectType.CYCLESEGMENT.getObjectType() };
		String query = dbUtil.formatQuery(FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION_ID, false, args);

		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			if (!result.hasNext()) {
				throw new DBException("Error!!Cycle segment is not found or has no members to reassociate!");
			}
			while (result.hasNext()) {

				BindingSet bindingSet = result.next();
				Value csaIRI = bindingSet.getValue(QUERY_VARIABLE_D);
				Value csIRI = bindingSet.getValue(QUERY_VARIABLE_OBJECTS);
				findCSAAndUpdate(contextVariables, csaIRI, csIRI, checkoutRevision, userId);
			}
		}
	}

	/**
	 * 
	 * @param contextVariables
	 * @param csaIRI
	 * @param csIRI
	 * @param checkoutRevision
	 * @param conn
	 */
	private void findCSAAndUpdate(ContextVariables contextVariables, Value csaIRI, Value csIRI, String checkoutRevision,
			String userId) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String casNS = dbUtil.fetchNameSpace(CYCLESEGMENTS_ASSOCIATION_NAMESPACE_VARIABLE, projectName);
		String csNameSpace = dbUtil.fetchNameSpace(CYCLESEGMENTS_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE).setNamespace(GENERIC_NS, csNameSpace)
				.setNamespace(GENERIC_NS_SECONDARY, casNS).setNamespace(RDFS.NS);

		// find new aggregates
		List<Integer> aggregateIds = contextVariables.getMembers();
		Map<Integer, Value> responseMap = fetchAggregatesForCS(projectName, crId, aggregateIds, userId);

		if (!responseMap.isEmpty()) {

			// delete existing csa
			builder.subject(checkoutRevision).add(ROOT_SUBJECT + DELETED, csaIRI);
			String uuid = UUID.randomUUID().toString();

			builder.subject(GENERIC_NS_SUB_SECONDARY + uuid).add(ROOT_SUBJECT + HAS_ID, uuid).add(RDFS.LABEL,
					CYCLE_SEGMENT_ASSOCIATION_LABEL);

			builder.subject(csIRI.stringValue()).add(ROOT_SUBJECT + HAS_CYCLE_SEGMENT_ASSOCIATION,
					GENERIC_NS_SUB_SECONDARY + uuid);

			builder.subject(checkoutRevision).add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB_SECONDARY + uuid);

			for (Integer aggregateId : aggregateIds) {
				if (responseMap.containsKey(aggregateId)) {
					builder.subject(GENERIC_NS_SUB_SECONDARY + uuid).add(ROOT_SUBJECT + CONTAINS,
							responseMap.get(aggregateId));
				} else {
					throw new DBException("Aggregate id (" + aggregateId + ") is not found!!");
				}
			}

		} else {
			throw new DBException("Aggregates to associate are not found!!");

		}

		finalRepo.buildModel(builder);
	}

}
