package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.KrawalBulkLoadDAO;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.model.Aggregate;
import com.siemens.krawal.krawalcloudmanager.model.Constraint;
import com.siemens.krawal.krawalcloudmanager.model.ControlUnit;
import com.siemens.krawal.krawalcloudmanager.model.CycleSegment;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;
import com.siemens.krawal.krawalcloudmanager.model.response.KrawalStructureObjectsResponse;
import com.siemens.krawal.krawalcloudmanager.model.response.LoadpointObjectsResponse;

@Component
public class KrawalBulkLoadManager {

	@Autowired
	private KrawalBulkLoadDAO bulkLoadDAO;

	/**
	 * 
	 * @param contextVariables
	 * @return
	 */
	public LoadpointObjectsResponse fetchAllLoadpointObjects(ContextVariables contextVariables) {

		LoadpointObjectsResponse loadpointObjectsResponse = new LoadpointObjectsResponse();
		Map<String, TupleQueryResult> resultMap = bulkLoadDAO.fetchAllLoadpointObjects(contextVariables);
		if (!CollectionUtils.isEmpty(resultMap)) {
			for (Map.Entry<String, TupleQueryResult> entry : resultMap.entrySet()) {
				try (TupleQueryResult queryResult = entry.getValue()) {
					if (entry.getKey().equalsIgnoreCase(ObjectType.CONSTRAINT.getObjectType())) {
						loadpointObjectsResponse.setConstraints(mapConstraints(queryResult));
					} else if (entry.getKey().equalsIgnoreCase(ObjectType.SIGNALCONNECTION.getObjectType())) {
						loadpointObjectsResponse.setSignalConnections(mapSignalConnections(queryResult));
					} else if (entry.getKey().equalsIgnoreCase(ObjectType.CONTROLUNIT.getObjectType())) {
						loadpointObjectsResponse.setControlUnits(mapControlUnits(queryResult));
					}
				}
			}
		}
		return loadpointObjectsResponse;
	}

	/**
	 * 
	 * @param contextVariables
	 * @return
	 */
	public KrawalStructureObjectsResponse fetchAllStuctureObjects(ContextVariables contextVariables) {

		KrawalStructureObjectsResponse response = new KrawalStructureObjectsResponse();
		Map<String, TupleQueryResult> resultMap = bulkLoadDAO.fetchAllStructureObjects(contextVariables);
		if (!CollectionUtils.isEmpty(resultMap)) {
			for (Map.Entry<String, TupleQueryResult> entry : resultMap.entrySet()) {
				try (TupleQueryResult queryResult = entry.getValue()) {
					if (entry.getKey().equalsIgnoreCase(ObjectType.AGGREGATE.getObjectType())) {
						response.setAggregates(mapAggregates(queryResult));
					} else if (entry.getKey().equalsIgnoreCase(ObjectType.MEDIUMCONNECTION.getObjectType())) {
						response.setMediumConnections(mapMediumConnections(queryResult));
					} else if (entry.getKey().equalsIgnoreCase(ObjectType.CYCLESEGMENT.getObjectType())) {
						response.setCycleSegments(mapCycleSegments(queryResult));
					}
				}
			}
		}
		return response;
	}

	/**
	 * 
	 * @param queryResult
	 * @return
	 */
	private List<Aggregate> mapAggregates(TupleQueryResult queryResult) {
		List<Aggregate> aggregates = new ArrayList<>();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();

			Value type = bindingSet.getValue("type");
			Value id = bindingSet.getValue("id");

			Aggregate aggregate = new Aggregate();
			aggregate.setKrawalId(Integer.parseInt(id.stringValue()));

			aggregate.setType(type.stringValue());

			aggregates.add(aggregate);

		}
		return aggregates;
	}

	/**
	 * 
	 * @param result
	 * @return
	 */
	private List<CycleSegment> mapCycleSegments(TupleQueryResult result) {
		List<CycleSegment> cycleSegments = new ArrayList<>();
		if (null != result) {
			Map<Integer, CycleSegment> cycleSegmentMap = new HashMap<>();

			while (result.hasNext()) {
				BindingSet bindingSet = result.next();

				Value aggregate = bindingSet.getValue("aggregateId");
				Value csId = bindingSet.getValue("id");
				int idVal = Integer.parseInt(csId.stringValue());
				setCycleSegment(cycleSegmentMap, idVal, aggregate);
			}
			if (!CollectionUtils.isEmpty(cycleSegmentMap)) {
				cycleSegments = cycleSegmentMap.values().stream().collect(Collectors.toList());
			}
		}

		return cycleSegments;
	}

	private void setCycleSegment(Map<Integer, CycleSegment> cycleSegmentMap, int id, Value aggregateId) {

		if (cycleSegmentMap.containsKey(id)) {
			int aggregateIdVal = Integer.parseInt(aggregateId.stringValue());
			cycleSegmentMap.get(id).getIds().add(aggregateIdVal);
		} else {
			CycleSegment cycleSegment = new CycleSegment();
			cycleSegment.setKrawalId(id);

			if (null != aggregateId) {
				List<Integer> aggregates = new ArrayList<>();
				aggregates.add(Integer.parseInt(aggregateId.stringValue()));
				cycleSegment.setIds(aggregates);
				cycleSegmentMap.put(id, cycleSegment);
			}
		}
	}

	/**
	 * 
	 * @param queryResult
	 * @return
	 */
	private List<MediumConnection> mapMediumConnections(TupleQueryResult queryResult) {

		List<MediumConnection> connections = new ArrayList<>();
		if (null != queryResult) {
			while (queryResult.hasNext()) {
				BindingSet bindingSet = queryResult.next();

				Value sourceId = bindingSet.getValue("sourceAggregateId");
				Value mcId = bindingSet.getValue("id");
				Value targetId = bindingSet.getValue("targetAggregateId");
				Value scPort = bindingSet.getValue("sourcePort");
				Value tPort = bindingSet.getValue("targetPort");
				Value mcType = bindingSet.getValue("type");
				setMediumConnection(sourceId, mcId, targetId, scPort, tPort, mcType, connections);
			}
		}

		return connections;
	}

	private void setMediumConnection(Value scId, Value mcId, Value tId, Value scPort, Value tPort, Value mcType,
			List<MediumConnection> mConnections) {
		MediumConnection connection = new MediumConnection();
		if (null != tId && null != tPort) {
			connection.setDestinationAggregateId(Integer.parseInt(tId.stringValue()));
			connection.setDestinationPort(tPort.stringValue());
		}
		connection.setKrawalId(Integer.parseInt(mcId.stringValue()));

		if (null != scId && null != scPort) {
			connection.setSourceAggregateId(Integer.parseInt(scId.stringValue()));
			connection.setSourcePort(scPort.stringValue());
		}
		connection.setType(mcType.stringValue());

		mConnections.add(connection);
	}

	/**
	 * 
	 * @param queryResult
	 * @return
	 */
	private List<Constraint> mapConstraints(TupleQueryResult queryResult) {
		List<Constraint> constraintList = new ArrayList<>();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();

			Value cType = bindingSet.getValue("type");
			Value cId = bindingSet.getValue("id");
			Value loadpointId = bindingSet.getValue("lpId");

			Constraint constraint = new Constraint();
			constraint.setKrawalId(Integer.parseInt(cId.stringValue()));
			constraint.setType(cType.stringValue());

			if (null != loadpointId) {
				constraint.setRefernceLoadpoint(Integer.parseInt(loadpointId.stringValue()));
			}
			constraintList.add(constraint);
		}
		return constraintList;
	}

	/**
	 * 
	 * @param queryResult
	 * @return
	 */
	private List<ControlUnit> mapControlUnits(TupleQueryResult queryResult) {
		List<ControlUnit> controlUnits = new ArrayList<>();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();

			Value cuType = bindingSet.getValue("type");
			Value cuId = bindingSet.getValue("id");

			ControlUnit controlUnit = new ControlUnit();
			controlUnit.setKrawalId(Integer.parseInt(cuId.stringValue()));

			controlUnit.setType(cuType.stringValue());

			controlUnits.add(controlUnit);
		}
		return controlUnits;
	}

	/**
	 * 
	 * @param queryResult
	 * @return
	 */
	private List<SignalConnection> mapSignalConnections(TupleQueryResult queryResult) {

		List<SignalConnection> connections = new ArrayList<>();
		Map<Integer, SignalConnection> scMap = new HashMap<>();
		while (queryResult.hasNext()) {
			BindingSet bindingSet = queryResult.next();

			Value toPort = bindingSet.getValue("appliedToPort");
			Value scId = bindingSet.getValue("id");
			Value viaPort = bindingSet.getValue("appliedViaPort");
			Value viaId = bindingSet.getValue("valuesId");
			Value type = bindingSet.getValue("valuesType");
			Value valuesR = bindingSet.getValue("valuesR");

			int idVal = Integer.parseInt(scId.stringValue());
			if (scMap.containsKey(idVal)) {
				mapConnections(scMap.get(idVal), viaId, type, valuesR);
			} else {
				SignalConnection connection = new SignalConnection();
				connection.setKrawalId(idVal);
				connection.setSourcePort(viaPort.stringValue());
				connection.setTargetPort(toPort.stringValue());
				mapConnections(connection, viaId, type, valuesR);
				scMap.put(idVal, connection);
			}
		}
		if (!CollectionUtils.isEmpty(scMap)) {
			connections = scMap.values().stream().collect(Collectors.toList());
		}
		return connections;
	}

	/**
	 * 
	 * @param signalConnection
	 * @param valuesId
	 * @param valuesType
	 * @param valueR
	 */
	private void mapConnections(SignalConnection signalConnection, Value valuesId, Value valuesType, Value valueR) {

		if (valueR.stringValue().contains("appliedVia")) {

			signalConnection.setSourceId(Integer.parseInt(valuesId.stringValue()));
			signalConnection.setSourceType(valuesType.stringValue());
		} else if (valueR.stringValue().contains("appliedTo")) {
			signalConnection.setTargetId(Integer.parseInt(valuesId.stringValue()));
			signalConnection.setTargetType(valuesType.stringValue());
		}
	}

}
