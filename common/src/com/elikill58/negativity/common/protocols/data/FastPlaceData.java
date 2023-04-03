package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class FastPlaceData extends CheckData {

	public int lastTick = 0;
	public double buffer = 0;
	
	public FastPlaceData(NegativityPlayer np) {
		super(np);
	}
	
	public void reduce() {
		if(buffer < 0.5)
			buffer = 0;
		else
			buffer *= 0.8;
	}
}
