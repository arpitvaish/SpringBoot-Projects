package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.Map;

import org.eclipse.rdf4j.query.TupleQueryResult;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface KrawalBulkLoadDAO {

	public Map<String, TupleQueryResult> fetchAllLoadpointObjects(ContextVariables contextVariables);

	public Map<String, TupleQueryResult> fetchAllStructureObjects(ContextVariables contextVariables);

}
