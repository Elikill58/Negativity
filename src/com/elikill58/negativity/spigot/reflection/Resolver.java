package com.elikill58.negativity.spigot.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Resolver {

	public static Method getSilentMethod(Class<?> clazz, String methodName, Class<?>... args) {
		try {
			clazz.getMethod(methodName, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field getSilentField(Class<?> clazz, String fieldname, Class<?>... args) {
		try {
			Field f = clazz.getField(fieldname);
			f.setAccessible(true);
			return f;
		} catch (Exception e) {
		}
		return null;
	}

	public static Field resolveByFirstType(Class<?> clazz, Class<?> type) throws ReflectiveOperationException {
		try {
			for (Field field : clazz.getDeclaredFields())
				if (field.getType().equals(type)) {
					field.setAccessible(true);
					return field;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
