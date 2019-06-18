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
import com.siemens.krawal.krawalcloudmanager.dao.ConstraintDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.Constraint;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.CONSTRAINT)
public class ConstraintManager implements LoadObjectManager {

	@Autowired
	private ConstraintDAO constraintDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		Constraint constraint = contextVariables.getConstraint();
		if (null != constraint && null != constraint.getType()) {
			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();
			int lpId = contextVariables.getLoadpontId();
			int referenceLP = constraint.getRefernceLoadpoint();
			contextVariables.setLoadpontId(lpId);
			String userId = contextVariables.getUserId();

			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			loadObjectDAO.verifyLoadpoint(contextVariables);

			int counter = contextVariables.getObjectCounter();
			String crIRI = contextVariables.getCheckoutRevisionIRI();
			constraintDAO.createConstraint(projectName, crId, counter, constraint.getType(), crIRI, lpId,userId);
			if(referenceLP > 0){
				constraintDAO.createReferenceToLpConstraint(contextVariables,referenceLP,counter);
			}
			
			return counter;

		} else {
			throw new RequestNotCompleteException("Required Parameters are missing - type");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		constraintDAO.fetchConstraints(contextVariables);
		List<Constraint> constraints = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();

				Value type = bindingSet.getValue("type");
				Value id = bindingSet.getValue("id");
				Value lpId = bindingSet.getValue("lpId");

				Constraint constraint = new Constraint();
				constraint.setKrawalId(Integer.parseInt(id.stringValue()));

				constraint.setType(type.stringValue());

				if (null != lpId) {
					constraint.setRefernceLoadpoint(Integer.parseInt(lpId.stringValue()));
				}
				constraints.add(constraint);
			}
		}
		return constraints;
	}

}
