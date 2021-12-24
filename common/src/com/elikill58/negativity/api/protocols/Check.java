package com.elikill58.negativity.api.protocols;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Check {

	/**
	 * Get the name of the check. Used to store in config files
	 * 
	 * @return the check name
	 */
	String name();
	
	/**
	 * All conditions of the check that should be (ALL) on true to run
	 * 
	 * @return all check conditions
	 */
	CheckConditions[] conditions() default {};

	/**
	 * The description of the check. Will be used in config file
	 * 
	 * @return the description of the check
	 */
	String description();

	/**
	 * This is for everytime check. It enable to get stats all time
	 * 
	 * @return true if should ignore everything
	 */
	boolean ignoreCancel() default false;
}
