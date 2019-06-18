package com.siemens.krawal.krawalcloudmanager.util;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.siemens.krawal.krawalcloudmanager.annotation.ObjectTypeAnnotation;
import com.siemens.krawal.krawalcloudmanager.dao.LoadObjectManager;

@Component
public class ServiceUtil {

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * 
	 * @param attributeType
	 * @return
	 */
	public LoadObjectManager findExactExtension(String attributeType) {
		LoadObjectManager executableClass = null;
		Map<String, LoadObjectManager> filters = applicationContext.getBeansOfType(LoadObjectManager.class);
		if (!CollectionUtils.isEmpty(filters)) {
			List<LoadObjectManager> filteredClasses = filters.values().stream().collect(Collectors.toList());
			Optional<LoadObjectManager> optionalClass = filteredClasses.stream()
					.filter(extensionAnnotationValue(attributeType)).findFirst();
			if (optionalClass.isPresent()) {
				executableClass = optionalClass.get();
			}
		}
		return executableClass;
	}

	/**
	 * 
	 * @param reqOperationValue
	 * @return
	 */
	private static Predicate<LoadObjectManager> extensionAnnotationValue(String reqOperationValue) {
		return p -> p.getClass().getAnnotation(ObjectTypeAnnotation.class).attributeType().toString()
				.equalsIgnoreCase(reqOperationValue);
	}

}
