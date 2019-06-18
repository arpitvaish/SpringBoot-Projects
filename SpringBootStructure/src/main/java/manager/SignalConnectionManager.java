package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.dao.SignalConnectionDAO;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.enums.SignalConnectionSourceType;
import com.siemens.krawal.krawalcloudmanager.enums.SignalConnectionTargetType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.exception.ValidationException;
import com.siemens.krawal.krawalcloudmanager.model.SignalConnection;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.SIGNALCONNECTION)
public class SignalConnectionManager implements LoadObjectManager {

	@Autowired
	private SignalConnectionDAO scDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		SignalConnection signalConnection = contextVariables.getSignalConnection();
		if (null != signalConnection && signalConnection.getSourceId() > 0 && signalConnection.getTargetId() > 0
				&& null != signalConnection.getSourceType() && null != signalConnection.getTargetType()) {
			validateAppliedVia(signalConnection.getSourceType());
			validateAppliedTo(signalConnection.getTargetType());

			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();
			String userId = contextVariables.getUserId();
			int lpId = contextVariables.getLoadpontId();
			contextVariables.setLoadpontId(lpId);

			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			loadObjectDAO.verifyLoadpoint(contextVariables);

			int counter = contextVariables.getObjectCounter();
			String crIRI = contextVariables.getCheckoutRevisionIRI();
			scDAO.createSignalConnection(projectName, crId, counter, crIRI, lpId, signalConnection, userId);
			return counter;

		} else {
			throw new RequestNotCompleteException("Required Parameters are missing - sourceId,targetId");
		}

	}

	public void validateAppliedVia(String type) {
		if (!Arrays.stream(SignalConnectionSourceType.values()).map(SignalConnectionSourceType::name)
				.collect(Collectors.toSet()).contains(type)) {
			throw new ValidationException("Type:" + type + " is not supported!!");
		}
	}

	public void validateAppliedTo(String type) {
		if (!Arrays.stream(SignalConnectionTargetType.values()).map(SignalConnectionTargetType::name)
				.collect(Collectors.toSet()).contains(type)) {
			throw new ValidationException("Type:" + type + " is not supported!!");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		scDAO.fetchSignalConnections(contextVariables);

		List<SignalConnection> signalConnections = new ArrayList<>();
		Map<Integer, SignalConnection> scMap = new HashMap<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();

				Value appliedToPort = bindingSet.getValue("appliedToPort");
				Value id = bindingSet.getValue("id");
				Value appliedViaPort = bindingSet.getValue("appliedViaPort");
				Value valuesId = bindingSet.getValue("valuesId");
				Value valuesType = bindingSet.getValue("valuesType");
				Value valuesR = bindingSet.getValue("valuesR");

				int idVal = Integer.parseInt(id.stringValue());
				if (scMap.containsKey(idVal)) {
					mapConnections(scMap.get(idVal), valuesId, valuesType, valuesR);
				} else {
					SignalConnection connection = new SignalConnection();
					connection.setKrawalId(idVal);
					connection.setSourcePort(appliedViaPort.stringValue());
					connection.setTargetPort(appliedToPort.stringValue());
					mapConnections(connection, valuesId, valuesType, valuesR);
					scMap.put(idVal, connection);
				}
			}
		}

		if (!CollectionUtils.isEmpty(scMap)) {
			signalConnections = scMap.values().stream().collect(Collectors.toList());
		}
		return signalConnections;
	}

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
