package com.siemens.krawal.krawalcloudmanager.manager;

import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.DESIGNDATA_LABEL;
import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.LOADDATA_LABEL;
import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.PLANTDATA_LABEL;
import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.RDFS_LABEL;
import static com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants.ROOT_NAMESPACE;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_DESIGN_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_LOAD_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_PLANT_DATA;
import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.HAS_UUID;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.DataParametersDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.model.response.DataResponse;

@Component
public class DataManager {

	@Autowired
	private LoadObjectDAO objectDAO;

	@Autowired
	private DataParametersDAO dao;

	public void setPlantData(ContextVariables contextVariables, Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		String crIRI = objectDAO.verifyRevision(projectName, crId, 0, userId);
		contextVariables.setCheckoutRevisionIRI(crIRI);
		dao.setPlantParameters(contextVariables, data);
	}

	public void setDesignData(ContextVariables contextVariables, Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		String crIRI = objectDAO.verifyRevision(projectName, crId, 0, userId);
		contextVariables.setCheckoutRevisionIRI(crIRI);
		dao.setDesignParameters(contextVariables, data);
	}

	public void setLoadData(ContextVariables contextVariables, Map<String, String> data) {

		String projectName = contextVariables.getProjectName();
		String crId = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();

		String crIRI = objectDAO.verifyRevision(projectName, crId, 0, userId);
		contextVariables.setCheckoutRevisionIRI(crIRI);

		objectDAO.verifyLoadpoint(contextVariables);
		dao.setLoadParameters(contextVariables, data);

	}

	public DataResponse getPlantData(ContextVariables contextVariables) {

		dao.fetchData(contextVariables, HAS_PLANT_DATA, PLANTDATA_LABEL);
		return getData(contextVariables.getResult(), contextVariables.getObjectId());

	}

	public DataResponse getDesignData(ContextVariables contextVariables) {

		dao.fetchData(contextVariables, HAS_DESIGN_DATA, DESIGNDATA_LABEL);
		return getData(contextVariables.getResult(), contextVariables.getObjectId());

	}

	public DataResponse getLoadData(ContextVariables contextVariables) {

		dao.fetchData(contextVariables, HAS_LOAD_DATA, LOADDATA_LABEL);
		return getData(contextVariables.getResult(), contextVariables.getObjectId());

	}

	public DataResponse getData(TupleQueryResult queryResult, int objectId) {

		DataResponse dataResponse = new DataResponse();
		if (null != queryResult) {
			Map<String, String> resultMap = new HashMap<>();
			try (TupleQueryResult result = queryResult) {
				if (!result.hasNext()) {
					throw new DBException("Data for (" + objectId + ") is not found.");
				}
				while (result.hasNext()) {
					BindingSet bindingSet = result.next();
					String relationString = bindingSet.getValue("relation").stringValue().replaceAll("%20", " ");
					String literalString = bindingSet.getValue("literalValue").stringValue();
					if (!(relationString.contains(RDFS_LABEL) || relationString.contains(HAS_UUID))) {
						relationString = relationString.substring(relationString.indexOf(ROOT_NAMESPACE)+ROOT_NAMESPACE.length());
						resultMap.put(relationString, literalString);
					}
				}
				dataResponse.setData(resultMap);
			}
		}
		return dataResponse;
	}

}
