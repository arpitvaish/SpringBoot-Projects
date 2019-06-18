package com.siemens.krawal.krawalcloudmanager.dao;

import java.util.Map;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

public interface DataParametersDAO {

	public void setPlantParameters(ContextVariables contextVariables, Map<String, String> data);

	public void setLoadParameters(ContextVariables contextVariables, Map<String, String> data);

	public void setDesignParameters(ContextVariables contextVariables, Map<String, String> data);

	public void fetchData(ContextVariables contextVariables, String relation, String label);
}
