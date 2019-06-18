package com.siemens.krawal.krawalcloudmanager.dao.impl;

import static com.siemens.krawal.krawalcloudmanager.db.constants.RelationshipConstants.DELETED;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_OBJECTS_TO_DELETE;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.FIND_PROJECT_OBJECT_COUNTER;
import static com.siemens.krawal.krawalcloudmanager.db.query.DBQueries.UPDATE_PROJECT_COUNTER;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.db.constants.DBConstants;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;
import com.siemens.krawal.krawalcloudmanager.exception.DBException;
import com.siemens.krawal.krawalcloudmanager.util.DBUtil;

@Component
public class LoadObjectDAOImpl implements LoadObjectDAO, DBConstants {

	@Autowired
	private DBUtil dbUtil;

	@Autowired
	private FinalRepositories finalRepositories;

	/**
	 * verify checkout revision & loadpoint
	 * 
	 * @param contextVariables
	 * @param rootNamespace
	 */
	public void verifyLoadpoint(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String checkoutRevisionId = contextVariables.getCheckoutRevisionId();
		int revisionNumber = contextVariables.getRevisionNumber();
		String userId = contextVariables.getUserId();
		int lpId = contextVariables.getLoadpontId();
		dbUtil.verifyLoadpoint(projectName, checkoutRevisionId, revisionNumber, lpId, userId);

	}

	/**
	 * update project counter which is the unique id generated for each object
	 * 
	 * @param projectName
	 * @param crID
	 * @param contextVariables
	 */
	public void validateProjectAndReturnCounter(ContextVariables contextVariables) {

		String projectName = contextVariables.getProjectName();
		String crID = contextVariables.getCheckoutRevisionId();
		String userId = contextVariables.getUserId();
		String revisionNamespace = dbUtil.fetchNameSpace(REVISIONS_NAMESPACE_VARIABLE, projectName);
		String cr = dbUtil.verifyRevision(projectName, crID, 0, userId);

		contextVariables.setCheckoutRevisionIRI(cr);
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace(ROOT, ROOT_NAMESPACE)
		.setNamespace(REV, revisionNamespace)
		.setNamespace(RDF.NS);

		setCounter(projectName, contextVariables);

	}

	/**
	 * code block that generates unique id for each object
	 * 
	 * @param query
	 * @param contextVariables
	 */
	private synchronized void setCounter(String projectName, ContextVariables contextVariables) {

		Object[] args = new String[] { projectName };
		String query = dbUtil.formatQuery(FIND_PROJECT_OBJECT_COUNTER, false, args);
		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {
			while (result.hasNext()) {
				BindingSet bindingSet = result.next();
				Value objectCounter = bindingSet.getValue(QUERYVARIABLE_OBJECTCOUNTER);
				Integer objCounter = Integer.parseInt(objectCounter.stringValue());
				Integer newValue = objCounter + 1;
				args = new Object[] { projectName, objCounter, projectName, newValue, projectName, objCounter };
				String updateQuery = dbUtil.formatQuery(UPDATE_PROJECT_COUNTER, false, args);
				finalRepositories.updateQuery(updateQuery);
				contextVariables.setObjectCounter(newValue);
			}
		}
	}

	public void deleteObject(String projectName, int objectId, String crId, String objectType, String userId) {

		int revNumber = Integer.parseInt(crId.split("_")[1]);

		String crIRI = dbUtil.verifyRevision(projectName, crId, 0, userId);
		objectType = dbUtil.capitalizeString(objectType);

		Object[] args = new Object[] { projectName, userId, objectId, objectType, revNumber, crId };
		String query = dbUtil.formatQuery(FIND_OBJECTS_TO_DELETE, false, args);

		try (TupleQueryResult result = finalRepositories.executeQuery(query)) {

			if (!result.hasNext()) {
				throw new DBException("Object with id (" + objectId + ") is not found to delete");
			}
			String revisionNamespace = dbUtil.fetchNameSpace(REVISIONS_NAMESPACE_VARIABLE, projectName);
			ModelBuilder builder = new ModelBuilder();
			builder.setNamespace(ROOT, ROOT_NAMESPACE).
			setNamespace(GENERIC_NS, revisionNamespace)
			.setNamespace(RDFS.NS);

			while (result.hasNext()) {

				BindingSet bindingSet = result.next();
				Value value = bindingSet.getValue("count");
				int countValue = Integer.parseInt(value.stringValue());
				Value objectIRI = bindingSet.getValue("object");
				Value propertiesIRI = bindingSet.getValue("b");

				if (countValue > 1) {
					throw new DBException("Object with id (" + objectId + ") is already deleted!");
				} else {
					builder.subject(crIRI).add(ROOT_SUBJECT + DELETED, objectIRI);
					if (objectType.equalsIgnoreCase(ObjectType.LOADPOINT.toString())
							|| propertiesIRI.stringValue().contains(GENERIC_DATA_NAMESPACE_VARIABLE)) {
						builder.subject(crIRI).add(ROOT_SUBJECT + DELETED, propertiesIRI);
					}
				}
			}
			finalRepositories.buildModel(builder);
		}

	}

	@Override
	public String verifyRevision(String projectName, String checkoutRevisionId, int revisionNo, String userId) {
		return dbUtil.verifyRevision(projectName, checkoutRevisionId, 0, userId);

	}

}
