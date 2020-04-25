package com.elikill58.negativity.universal.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {
	
	public static Object getPrivateField(Object object, String field)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field objectField = object.getClass().getDeclaredField(field);
		objectField.setAccessible(true);
		return objectField.get(object);
	}
}
