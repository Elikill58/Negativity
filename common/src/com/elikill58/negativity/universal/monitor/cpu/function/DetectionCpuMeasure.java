package com.elikill58.negativity.universal.monitor.cpu.function;

import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;

public class DetectionCpuMeasure extends CpuMeasure<Check> {

	public DetectionCpuMeasure(IDetectionKey<?> key) {
		super(key.getName());
	}

	@Override
	public String getName(Check o) {
		return o.name();
	}
}
