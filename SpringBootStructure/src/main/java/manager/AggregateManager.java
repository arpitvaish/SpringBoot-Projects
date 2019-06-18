package com.siemens.krawal.krawalcloudmanager.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.AggregateDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.Aggregate;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.AGGREGATE)
public class AggregateManager implements LoadObjectManager {

	@Autowired
	private AggregateDAO aggregateDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		Aggregate aggregate = contextVariables.getAggregate();
		if (null != aggregate && null != aggregate.getType()) {
			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();

			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			int counter = contextVariables.getObjectCounter();
			aggregateDAO.createAggregate(projectName, crId, counter, aggregate.getType(),
					contextVariables.getCheckoutRevisionIRI());
			return counter;
		} else {
			throw new RequestNotCompleteException("Required Parameters are missing - type");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		aggregateDAO.fetchAggregates(contextVariables);

		List<Aggregate> aggregates = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			if(null != result) {
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();

					Value type = bindingSet.getValue("type");
					Value id = bindingSet.getValue("id");

					Aggregate aggregate = new Aggregate();
					if (null != id) {
						aggregate.setKrawalId(Integer.parseInt(id.stringValue()));
					}
					if (null != type) {
						aggregate.setType(type.stringValue());
					}
					aggregates.add(aggregate);
				}
			}

		}

		return aggregates;
	}

	/**
	 * 
	 * @param contextVariables
	 */
	public void assemble(ContextVariables contextVariables) {

		String userId = contextVariables.getUserId();
		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String cr = loadObjectDAO.verifyRevision(projectName, crId, 0, userId);
		contextVariables.setCheckoutRevisionIRI(cr);
		aggregateDAO.assemble(contextVariables);

	}

	/**
	 * 
	 * @param contextVariables
	 * @return
	 */
	public Set<Integer> getAssembleParts(ContextVariables contextVariables) {

		aggregateDAO.getAssemblyParts(contextVariables);
		Set<Integer> aggregateIds = new HashSet<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			if (null != result) {
				while (result.hasNext()) {

					BindingSet bindingSet = result.next();
					Value aggregateId = bindingSet.getValue("id");
					aggregateIds.add(Integer.parseInt(aggregateId.stringValue()));
				}
			}
		}
		return aggregateIds;

	}

}
