package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class SneakData extends CheckData {

	public boolean wasSneaking = false, lastSecond = false;
	public double buffer = 0;
	
	public SneakData(NegativityPlayer np) {
		super(np);
	}
}
