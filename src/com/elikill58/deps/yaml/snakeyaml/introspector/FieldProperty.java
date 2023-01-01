package com.elikill58.deps.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import com.elikill58.deps.yaml.snakeyaml.error.YAMLException;
import com.elikill58.deps.yaml.snakeyaml.util.ArrayUtils;

public class FieldProperty extends GenericProperty {
	private final Field field;

	public FieldProperty(final Field field) {
		super(field.getName(), field.getType(), field.getGenericType());
		(this.field = field).setAccessible(true);
	}

	@Override
	public void set(final Object object, final Object value) throws Exception {
		this.field.set(object, value);
	}

	@Override
	public Object get(final Object object) {
		try {
			return this.field.get(object);
		} catch (Exception e) {
			throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + e);
		}
	}

	@Override
	public List<Annotation> getAnnotations() {
		return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
	}

	@Override
	public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
		return this.field.getAnnotation(annotationType);
	}
}
