package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class MotionData extends CheckData {

	public int buffer = 0;
	
	public MotionData(NegativityPlayer np) {
		super(np);
	}
}
