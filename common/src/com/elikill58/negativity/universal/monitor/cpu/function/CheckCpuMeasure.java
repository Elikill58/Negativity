package com.elikill58.negativity.universal.monitor.cpu.function;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;

public class CheckCpuMeasure extends CpuMeasure<Check> {

	public CheckCpuMeasure(IDetectionKey<?> key) {
		super(key.getName());
	}
	
	@Override
	public @NonNull String getVisualName(Check o) {
		return o.name();
	}

	@Override
	public String getName(Check o) {
		Cheat c = Cheat.values().stream().filter(cc -> cc.getChecks().contains(o)).findFirst().get();
		return (c == null ? "others" : c.getKey().getLowerKey()) + "." + o.name();
	}
}
