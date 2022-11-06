package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class RegenData extends CheckData {

	public long lastRegen = 0;
	
	public RegenData(NegativityPlayer np) {
		super(np);
	}
}
