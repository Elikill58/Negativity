package com.elikill58.negativity.universal.monitor.cpu.function;

import java.lang.reflect.Method;

public class EventCpuMeasure extends CpuMeasure<Method> {

	public EventCpuMeasure(Class<?> e) {
		super(e.getClass().getSimpleName());
	}
	
	@Override
	public String getName(Method o) {
		return o.getDeclaringClass().getName().replace("com.elikill58.negativity", "negativity").replace('.', '_') + "." + o.getName();
	}
}
