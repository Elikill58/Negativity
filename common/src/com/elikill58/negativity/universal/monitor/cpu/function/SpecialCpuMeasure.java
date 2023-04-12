package com.elikill58.negativity.universal.monitor.cpu.function;

import java.lang.reflect.Method;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.universal.detections.Special;

public class SpecialCpuMeasure extends CpuMeasure<Method> {

	private final Special special;
	
	public SpecialCpuMeasure(Special key) {
		super(key.getName());
		this.special = key;
	}

	@Override
	public @NonNull String getVisualName(Method o) {
		return o.getName();
	}
	
	@Override
	public String getName(Method o) {
		return special.getKey().getLowerKey() + "." + o.getName();
	}
}
