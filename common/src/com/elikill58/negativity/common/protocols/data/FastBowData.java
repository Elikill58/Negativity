package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class FastBowData extends CheckData {

	public long lastShot = 0;
	
	public FastBowData(NegativityPlayer np) {
		super(np);
	}

}
