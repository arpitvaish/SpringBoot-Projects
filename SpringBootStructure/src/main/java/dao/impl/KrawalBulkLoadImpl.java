package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CONSTRAINT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_CONTROLUNIT;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_SIGNAL_CONNECTION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_AGGREGATES_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_AGGREGATES_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONNECTIONS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONNECTIONS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONSTRAINTS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONSTRAINTS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONTROL_UNITS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CONTROL_UNITS_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_CYCLE_SEGMENT_COMMITTED_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_SIGNAL_CONNECTIONS_CHECKOUT_REVISION;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FETCH_SIGNAL_CONNECTIONS_COMMITTED_REVISION;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.rdf4j.query.TupleQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.KrawalBulkLoadDAO;
import com.siemens.krawal.krawalcloudmanager.dao.concurrency.LoadpointCallableStrategy;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class KrawalBulkLoadImpl implements KrawalBulkLoadDAO {

	private static final Logger LOGGER = LoggerFactory.getLogger(KrawalBulkLoadImpl.class);

	@Autowired
	private FinalRepositories finalRepo;
	
	@Autowired
	private DBUtil dbUtil;

	@Override
	public Map<String, TupleQueryResult> fetchAllLoadpointObjects(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();
		int lpId = contextVariables.getLoadpontId();

		int revisionNumber = contextVariables.getRevisionNumber();
		String fetchConstraintsQuery = null;
		String fetchSignalConnectionsQuery = null;
		String fetchControlUnitsQuery = null;

		verifyRevisionAndLoadpoint(contextVariables);
		Map<String, TupleQueryResult> resultMap = new HashMap<>();

		if (null != crId) {
			revisionNumber = Integer.parseInt(crId.split("_")[1]);
			Object[] args = new Object[] { projectName, revisionNumber, crId, projectName, userId, lpId,
					HAS_CONSTRAINT, revisionNumber, crId, revisionNumber, crId };
			fetchConstraintsQuery = dbUtil.formatQuery(FETCH_CONSTRAINTS_CHECKOUT_REVISION, false, args);
			args = new Object[] { projectName, revisionNumber, crId, projectName, userId, lpId, HAS_CONTROLUNIT, revisionNumber, crId };
			fetchControlUnitsQuery = dbUtil.formatQuery(FETCH_CONTROL_UNITS_CHECKOUT_REVISION, false, args);
			args = new Object[] { projectName, revisionNumber, crId, projectName, userId, lpId,
					HAS_SIGNAL_CONNECTION, revisionNumber, crId, revisionNumber, crId };
			fetchSignalConnectionsQuery = dbUtil.formatQuery(FETCH_SIGNAL_CONNECTIONS_CHECKOUT_REVISION, false, args);
			
		} else if (revisionNumber > 0) {
			Object[] args = new Object[] { projectName, revisionNumber, projectName, userId, lpId,
					HAS_CONSTRAINT, revisionNumber, revisionNumber };
			fetchConstraintsQuery = dbUtil.formatQuery(FETCH_CONSTRAINTS_COMMITTED_REVISION, false, args);
			args = new Object[] { projectName, revisionNumber, projectName, userId, lpId, HAS_CONTROLUNIT, revisionNumber };
			fetchControlUnitsQuery = dbUtil.formatQuery(FETCH_CONTROL_UNITS_COMMITTED_REVISION, false, args);
			args = new Object[] { projectName, revisionNumber, projectName, userId, lpId, HAS_SIGNAL_CONNECTION, revisionNumber, revisionNumber };
			fetchSignalConnectionsQuery = dbUtil.formatQuery(FETCH_SIGNAL_CONNECTIONS_COMMITTED_REVISION, false, args);
		}
		ExecutorService executor = Executors.newFixedThreadPool(4);
		try {

			Callable<TupleQueryResult> callableConstraints = new LoadpointCallableStrategy(fetchConstraintsQuery, finalRepo);
			Future<TupleQueryResult> futureConstraints = executor.submit(callableConstraints);
			LOGGER.info("Thread 1:Constraints");

			Callable<TupleQueryResult> callableSC = new LoadpointCallableStrategy(fetchSignalConnectionsQuery, finalRepo);
			Future<TupleQueryResult> futureSC = executor.submit(callableSC);
			LOGGER.info("Thread 2:Signal Connections");

			Callable<TupleQueryResult> callableCU = new LoadpointCallableStrategy(fetchControlUnitsQuery, finalRepo);
			Future<TupleQueryResult> futureCU = executor.submit(callableCU);
			LOGGER.info("Thread 3:Control Units");

			TupleQueryResult constraintResult = futureConstraints.get();
			resultMap.put(ObjectType.CONSTRAINT.getObjectType(), constraintResult);
			TupleQueryResult scResult = futureSC.get();
			resultMap.put(ObjectType.SIGNALCONNECTION.getObjectType(), scResult);
			TupleQueryResult cuResult = futureCU.get();
			resultMap.put(ObjectType.CONTROLUNIT.getObjectType(), cuResult);

		} catch (InterruptedException | ExecutionException e) {
			throw new DBException("Fetching loadpoint objects is failed");

		} finally {
			executor.shutdown();
		}

		return resultMap;
	}

	@Override
	public Map<String, TupleQueryResult> fetchAllStructureObjects(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		int revisionNumber = contextVariables.getRevisionNumber();
		String fetchAggregatesQuery = null;
		String fetchCycleSegmentsQuery = null;
		String fetchConnectionsQuery = null;

		Map<String, TupleQueryResult> resultMap = new HashMap<>();

		if (null != crId) {
			dbUtil.verifyRevision(projectName, crId, 0, userId);
			revisionNumber = Integer.parseInt(crId.split("_")[1]);
			Object[] args = new Object[] { revisionNumber, crId, projectName, userId, revisionNumber, crId };
			fetchAggregatesQuery = dbUtil.formatQuery(FETCH_AGGREGATES_CHECKOUT_REVISION, false, args);
			args = new Object[] { revisionNumber, crId, projectName, userId, revisionNumber, crId, revisionNumber, crId };
			fetchCycleSegmentsQuery = dbUtil.formatQuery(FETCH_CYCLE_SEGMENT_CHECKOUT_REVISION, false, args);
			args = new Object[] { revisionNumber, crId, projectName, userId, revisionNumber, crId, revisionNumber, crId };
			fetchConnectionsQuery = dbUtil.formatQuery(FETCH_CONNECTIONS_CHECKOUT_REVISION, false, args);

		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			Object[] args = new Object[] { revisionNumber, projectName, userId, revisionNumber };
			fetchAggregatesQuery = dbUtil.formatQuery(FETCH_AGGREGATES_COMMITTED_REVISION, false, args);
			args = new Object[] { revisionNumber, projectName, userId, revisionNumber, revisionNumber };
			fetchCycleSegmentsQuery = dbUtil.formatQuery(FETCH_CYCLE_SEGMENT_COMMITTED_REVISION, false, args);
			args = new Object[] { revisionNumber, projectName, userId, revisionNumber, revisionNumber };
			fetchConnectionsQuery = dbUtil.formatQuery(FETCH_CONNECTIONS_COMMITTED_REVISION, false, args);
		}
		ExecutorService executor = Executors.newFixedThreadPool(3);
		try {

			Callable<TupleQueryResult> callableAggregates = new LoadpointCallableStrategy(fetchAggregatesQuery, finalRepo);
			Future<TupleQueryResult> futureAggregates = executor.submit(callableAggregates);
			LOGGER.info("Thread 1:Constraints");

			Callable<TupleQueryResult> callableCS = new LoadpointCallableStrategy(fetchCycleSegmentsQuery, finalRepo);
			Future<TupleQueryResult> futureCS = executor.submit(callableCS);
			LOGGER.info("Thread 2:Signal Connections");

			Callable<TupleQueryResult> callableMC = new LoadpointCallableStrategy(fetchConnectionsQuery, finalRepo);
			Future<TupleQueryResult> futureMC = executor.submit(callableMC);
			LOGGER.info("Thread 3:Control Units");

			TupleQueryResult aggregateResult = futureAggregates.get();
			resultMap.put(ObjectType.AGGREGATE.getObjectType(), aggregateResult);
			TupleQueryResult csResult = futureCS.get();
			resultMap.put(ObjectType.CYCLESEGMENT.getObjectType(), csResult);
			TupleQueryResult mcResult = futureMC.get();
			resultMap.put(ObjectType.MEDIUMCONNECTION.getObjectType(), mcResult);

		} catch (InterruptedException | ExecutionException e) {
			throw new DBException("Fetching krawal structure objects is failed");

		} finally {
			executor.shutdown();
		}

		return resultMap;

	}

	private void verifyRevisionAndLoadpoint(ContextVariables contextVariables) {
		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();
		int loadpointId = contextVariables.getLoadpontId();
		int revisionNumber = contextVariables.getRevisionNumber();
		if (null != crId) {
			dbUtil.verifyRevision(projectName, crId, 0, userId);
			dbUtil.verifyLoadpoint(projectName, crId, 0, loadpointId, userId);
		} else if (revisionNumber > 0) {
			dbUtil.verifyRevision(projectName, null, revisionNumber, userId);
			dbUtil.verifyLoadpoint(projectName, null, revisionNumber, loadpointId, userId);
		}
	}

}
