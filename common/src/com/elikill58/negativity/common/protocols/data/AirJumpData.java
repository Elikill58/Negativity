package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class AirJumpData extends CheckData {

	public double diffY = 0;
	public boolean goingDown = false;
	
	public AirJumpData(NegativityPlayer np) {
		super(np);
	}
}
