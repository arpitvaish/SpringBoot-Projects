package com.siemens.krawal.krawalcloudmanager.dao;

import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;

/**
 * strategic design pattern
 *
 */
public interface LoadObjectManager {

	public int create(ContextVariables contextVariables);

	public Object fetch(ContextVariables contextVariables);
	
	public default void delete(String projectName, String checkoutRevisionid, int objectId, String objectType, String userId) {
		
	}
}
