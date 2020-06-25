package com.elikill58.negativity.universal.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
	
	public static Object getPrivateField(Object object, String field)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field objectField = object.getClass().getDeclaredField(field);
		objectField.setAccessible(true);
		return objectField.get(object);
	}
	
	public static void setField(Object src, String fieldName, Object value) {
		try {
			Field field = src.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(src, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the specified field name in the object source
	 * 
	 * @param source where we will find the field
	 * @param field the name of the field
	 * @return the requested field
	 */
	public static Object getField(Object source, String field) {
		try {
			Field f = source.getClass().getField(field);
			f.setAccessible(true);
			return f.get(source);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Call method. Return the method return.
	 * 
	 * @param source the object where we want to run the method
	 * @param method the name of the method to call
	 * @return the return of the method called
	 */
	public static Object callMethod(Object source, String method) {
		try {
			return source.getClass().getDeclaredMethod(method).invoke(source);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
