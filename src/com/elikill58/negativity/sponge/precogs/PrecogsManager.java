package com.elikill58.negativity.sponge.precogs;

import java.util.Locale;

import com.elikill58.negativity.universal.Cheat;
import com.me4502.precogs.detection.DetectionType;

public class PrecogsManager {
	
	public static DetectionType toDetectionType(Cheat c) {
		return new DetectionType(c.getKey().toLowerCase(Locale.ROOT), c.getName());
	}
}
