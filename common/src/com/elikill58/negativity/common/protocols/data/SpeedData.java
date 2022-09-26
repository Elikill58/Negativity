package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class SpeedData extends CheckData {

	public int oldSpeedLevel = 0, oldSlowLevel = 0, highSpeedAmount = 0;
	public double sameDiffY = 0;
	
	public SpeedData(NegativityPlayer np) {
		super(np);
	}
}
