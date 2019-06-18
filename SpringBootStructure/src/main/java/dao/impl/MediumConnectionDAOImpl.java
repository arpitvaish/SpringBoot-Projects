package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.CREATED;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_ID;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_TYPE;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.INGRES_PORT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.OUTGRES_PORT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.VIA;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONNECTIONS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONNECTIONS_COMMITTED_REVISION;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.MediumConnectionDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.db.query.DBQueries;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class MediumConnectionDAOImpl implements MediumConnectionDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepo;

	@Override
	public void createMediumConnection(String projectName, String crId, int objectCounter, MediumConnection connection,
			String checkoutRevision, String userId) {

		int sourceAggregateId = connection.getSourceAggregateId();
		int destinationAggregateId = connection.getDestinationAggregateId();
		int revNumber = Integer.parseInt(crId.split("_")[1]);

		Map<Integer, String> aggregateIds = new HashMap<>();

		if (sourceAggregateId > 0) {
			aggregateIds.put(sourceAggregateId, connection.getSourcePort());
		}
		if (destinationAggregateId > 0) {
			aggregateIds.put(destinationAggregateId, connection.getDestinationPort());
		}

		String mcNameSpace = dbUtil.fetchNameSpace(MEDIUMCONNECTIONS_NAMESPACE_VARIABLE, projectName);
		String aggregateNameSpace = dbUtil.fetchNameSpace(AGGREGATES_NAMESPACE_VARIABLE, projectName);

		ModelBuilder builder = new ModelBuilder();

		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(GENERIC_NS, mcNameSpace)
		.setNamespace(GENERIC_NS_SECONDARY, aggregateNameSpace)
		.setNamespace(RDF.NS);

		builder.subject(GENERIC_NS_SUB + objectCounter)
		.add(DBConstants.ROOT_SUBJECT + HAS_ID, objectCounter)
		.add(DBConstants.ROOT_SUBJECT + HAS_TYPE, connection.getType());

		builder.subject(checkoutRevision)
		.add(ROOT_SUBJECT + CREATED, GENERIC_NS_SUB + objectCounter);

		Object[] args = new Object[] { revNumber, crId, projectName, userId, revNumber, crId,
				aggregateIds.keySet().stream().collect(Collectors.toList()), AGGREGATE_LABEL };
		BindingSet bindingSet = null;
		String query = dbUtil.formatQuery(DBQueries.FIND_AGGREGATES, false, args);
		Map<Integer, Value> responseMap = new HashMap<>();

		try (TupleQueryResult result = finalRepo.executeQuery(query)) {
			while (result.hasNext()) {
				bindingSet = result.next();
				Value aggregateIRI = bindingSet.getValue(QUERYVARIABLE_AGGREGATE);
				Value id = bindingSet.getValue(QUERYVARIABLE_ID);
				int aggregateId = Integer.parseInt(id.stringValue());
				responseMap.put(aggregateId, aggregateIRI);
			}
		}

		if (sourceAggregateId > 0) {
			if (responseMap.containsKey(sourceAggregateId)) {
				builder.subject(GENERIC_NS_SUB + objectCounter)
				.add(ROOT_SUBJECT + VIA, responseMap.get(sourceAggregateId))
				.add(ROOT_SUBJECT + INGRES_PORT, aggregateIds.get(sourceAggregateId))
				.add(RDFS.LABEL, MEDIUM_CONNECTION_LABEL);
			} else {
				throw new DBException("Aggregate id (" + sourceAggregateId + ") is not found!!");
			}
		}

		if (destinationAggregateId > 0) {
			if (responseMap.containsKey(destinationAggregateId)) {
				builder.subject(GENERIC_NS_SUB + objectCounter)
				.add(ROOT_SUBJECT + OUTGRES_PORT,aggregateIds.get(destinationAggregateId));

				builder.subject(GENERIC_NS_SUB_SECONDARY + destinationAggregateId)
				.add(ROOT_SUBJECT + VIA,GENERIC_NS_SUB + objectCounter);
			} else {
				throw new DBException("Aggregate id (" + destinationAggregateId + ") is not found!!");
			}
		}

		finalRepo.buildModel(builder);
	}

	@Override
	public void fetchMediumConnections(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		int revisionNumber = contextVariables.getRevisionNumber();

		String fetchQuery = null;
		if (null != checkoutRevisionId) {
			dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);
			revisionNumber = Integer.parseInt(checkoutRevisionId.split("_")[1]);
			Object[] args = new Object[] { revisionNumber, checkoutRevisionId, projectName, userId, revisionNumber, checkoutRevisionId, revisionNumber, checkoutRevisionId};
			fetchQuery = dbUtil.formatQuery(FETCH_CONNECTIONS_CHECKOUT_REVISION, false, args);

		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			Object[] args = new Object[] { revisionNumber, projectName, userId, revisionNumber, revisionNumber };
			fetchQuery = dbUtil.formatQuery(FETCH_CONNECTIONS_COMMITTED_REVISION, false, args);
		}

		contextVariables.setResult(finalRepo.executeQuery(fetchQuery));

	}

}
