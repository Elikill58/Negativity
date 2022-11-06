package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class NoFallData extends CheckData {

	public float lastFloat = 0;
	public boolean useAntiNofall = false;
	
	public NoFallData(NegativityPlayer np) {
		super(np);
	}
}
