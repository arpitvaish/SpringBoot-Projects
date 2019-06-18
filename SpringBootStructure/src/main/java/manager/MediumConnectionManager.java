package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.dao.MediumConnectionDAO;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.MediumConnection;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.MEDIUMCONNECTION)
public class MediumConnectionManager implements LoadObjectManager {

	@Autowired
	private MediumConnectionDAO connectionDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		MediumConnection connection = contextVariables.getMediumConnection();
		if (null != connection && null != connection.getType() && connection.getSourceAggregateId() > 0
				&& null != connection.getSourcePort()) {
			if (connection.getDestinationAggregateId() > 0) {
				if (null == connection.getDestinationPort()) {
					throw new RequestNotCompleteException(
							"Required Parameters are missing - atleast type,source/destination aggregateId & port should be present!");
				}
			}
			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();
			String userId = contextVariables.getUserId();
			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			int counter = contextVariables.getObjectCounter();
			connectionDAO.createMediumConnection(projectName, crId, counter, connection,
					contextVariables.getCheckoutRevisionIRI(), userId);
			return counter;
		} else {
			throw new RequestNotCompleteException(
					"Required Parameters are missing - atleast type,sourceAggregateId, sourcePort should be present!");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		connectionDAO.fetchMediumConnections(contextVariables);

		List<MediumConnection> connections = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			if (null != result) {
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();

					Value sourceAggregateId = bindingSet.getValue("sourceAggregateId");
					Value id = bindingSet.getValue("id");
					Value targetAggregateId = bindingSet.getValue("targetAggregateId");
					Value sourcePort = bindingSet.getValue("sourcePort");
					Value targetPort = bindingSet.getValue("targetPort");
					Value type = bindingSet.getValue("type");

					MediumConnection connection = new MediumConnection();
					if (null != targetAggregateId && null != targetPort) {
						connection.setDestinationAggregateId(Integer.parseInt(targetAggregateId.stringValue()));
						connection.setDestinationPort(targetPort.stringValue());
					}
					connection.setKrawalId(Integer.parseInt(id.stringValue()));

					if (null != sourceAggregateId && null != sourcePort) {
						connection.setSourceAggregateId(Integer.parseInt(sourceAggregateId.stringValue()));
						connection.setSourcePort(sourcePort.stringValue());
					}
					connection.setType(type.stringValue());

					connections.add(connection);
				}
			}
		}

		return connections;
	}

}
