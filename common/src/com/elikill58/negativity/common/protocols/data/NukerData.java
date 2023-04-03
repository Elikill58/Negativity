package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class NukerData extends CheckData {

	public int ticks = 0;
	
	public NukerData(NegativityPlayer np) {
		super(np);
	}
}
