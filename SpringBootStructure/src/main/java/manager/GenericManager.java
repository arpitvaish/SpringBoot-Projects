package com.siemens.krawal.krawalcloudmanager.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.context.ContextVariables;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectDAO;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;
import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;

@Component
@Primary
@ObjectTypeAnnotation(attributeType = ObjectType.NA)
public class GenericManager implements LoadObjectManager {

	@Autowired
	private LoadObjectDAO objectDAO;

	@Override
	public int create(ContextVariables contextVariables) {
		return 0;
	}

	@Override
	public Object fetch(ContextVariables contextVariables) {
		return null;
	}

	@Override
	public void delete(String projectName, String checkoutRevisionid, int objectId, String objectType,String userId) {
		objectDAO.deleteObject(projectName, objectId, checkoutRevisionid, objectType,userId);
	}
}
