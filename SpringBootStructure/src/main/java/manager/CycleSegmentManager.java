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

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.CycleSegmentDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.RequestNotCompleteException;
import com.siemens.krawal.krawalcloudmanager.model.CycleSegment;

@Component
@ObjectTypeAnnotation(attributeType = ObjectType.CYCLESEGMENT)
public class CycleSegmentManager implements LoadObjectManager {

	@Autowired
	private CycleSegmentDAO cycleSegmentDAO;

	@Autowired
	private LoadObjectDAO loadObjectDAO;

	@Override
	public int create(ContextVariables contextVariables) {

		CycleSegment cycleSegment = contextVariables.getCycleSegment();
		if (null != cycleSegment && !CollectionUtils.isEmpty(cycleSegment.getIds())) {
			String projectName = contextVariables.getProjectName();
			String crId = contextVariables.getCheckoutRevisionId();
			String userId = contextVariables.getUserId();

			loadObjectDAO.validateProjectAndReturnCounter(contextVariables);
			int counter = contextVariables.getObjectCounter();
			cycleSegmentDAO.createCycleSegment(projectName, crId, counter, cycleSegment.getIds(),
					contextVariables.getCheckoutRevisionIRI(), userId);
			return counter;
		} else {
			throw new RequestNotCompleteException("Required Parameters are missing - ids");
		}
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {

		cycleSegmentDAO.fetchCycleSegments(contextVariables);
		List<CycleSegment> cycleSegments = new ArrayList<>();
		try (TupleQueryResult result = contextVariables.getResult()) {
			if (null != result) {
				Map<Integer, CycleSegment> csMap = new HashMap<>();

				while (result.hasNext()) {
					BindingSet bindingSet = result.next();

					Value aggregateId = bindingSet.getValue("aggregateId");
					Value id = bindingSet.getValue("id");

					int idVal = Integer.parseInt(id.stringValue());
					if (csMap.containsKey(idVal)) {
						int aggregateIdVal = Integer.parseInt(aggregateId.stringValue());
						csMap.get(idVal).getIds().add(aggregateIdVal);
					} else {
						CycleSegment cycleSegment = new CycleSegment();
						cycleSegment.setKrawalId(idVal);

						if (null != aggregateId) {
							List<Integer> aggregateIds = new ArrayList<>();
							aggregateIds.add(Integer.parseInt(aggregateId.stringValue()));
							cycleSegment.setIds(aggregateIds);
						}
						csMap.put(idVal, cycleSegment);
					}
				}
				if (!CollectionUtils.isEmpty(csMap)) {
					cycleSegments = csMap.values().stream().collect(Collectors.toList());
				}
			}
		}

		return cycleSegments;
	}

	public void joinCycleSegment(ContextVariables contextVariables) {

		cycleSegmentDAO.joinCycleSegment(contextVariables);

	}

}
