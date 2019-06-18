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
import com.siemens.krawal.krawalcloudmanager.dao.LoadpointDAO;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.model.Loadpoint;
import com.siemens.krawal.krawalcloudmanager.model.response.FetchAllLoadpointsResponse;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.LOADPOINT)
public class LoadpointManager implements LoadObjectManager {

	@Autowired
	private LoadpointDAO loadpointDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();

		loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
		int counter = contextVariables.getObjectCounter();
		loadpointDAO.createLoadpoint(projectName, crId, counter, contextVariables.getCheckoutRevisionIRI());
		return counter;
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		loadpointDAO.fetchLoadpoints(contextVariables);

		List<Loadpoint> loadpoints = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			if (null != result) {
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					Value id = bindingSet.getValue("id");

					Loadpoint loadpoint = new Loadpoint();
					loadpoint.setKrawalId(Integer.parseInt(id.stringValue()));

					loadpoints.add(loadpoint);
				}
			}
		}

		FetchAllLoadpointsResponse response = new FetchAllLoadpointsResponse();
		response.setLoadpoints(loadpoints);
		return response;
	}

}
