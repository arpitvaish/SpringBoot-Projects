package com.siemens.krawal.krawalcloudmanager.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.siemens.krawal.krawalcloudmanager.enums.ObjectType;

@Retention(RetentionPolicy.RUNTIME)
public @interface ObjectTypeAnnotation {
	ObjectType attributeType() default ObjectType.AGGREGATE;
	
}

