package com.elikill58.negativity.api.protocols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Check {

	String name();
	
	CheckConditions[] conditions() default {};

	String description();

}
