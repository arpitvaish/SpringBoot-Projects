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
import com.siemens.krawal.krawalcloudmanager.dao.ControlUnitDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.ControlUnit;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.CONTROLUNIT)
public class ControlUnitManager implements LoadObjectManager {

	@Autowired
	private ControlUnitDAO controlUnitDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		ControlUnit controlUnit = contextVariables.getControlUnit();
		if (null != controlUnit && null != controlUnit.getType()) {
			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();
			int lpId = contextVariables.getLoadpontId();
			contextVariables.setLoadpontId(lpId);

			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			loadObjectDAO.verifyLoadpoint(contextVariables);
			int counter = contextVariables.getObjectCounter();
			String crIRI = contextVariables.getCheckoutRevisionIRI();
			controlUnitDAO.createControlUnit(projectName, crId, counter, controlUnit.getType(), crIRI, lpId);
			return counter;
		} else {
			throw new RequestNotCompleteException("Required Parameters are missing - type");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {
		controlUnitDAO.fetchControlUnits(contextVariables);

		List<ControlUnit> controlUnits = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();

				Value type = bindingSet.getValue("type");
				Value id = bindingSet.getValue("id");

				ControlUnit controlUnit = new ControlUnit();
				controlUnit.setKrawalId(Integer.parseInt(id.stringValue()));

				controlUnit.setType(type.stringValue());

				controlUnits.add(controlUnit);
			}
		}

		return controlUnits;
	}

}
