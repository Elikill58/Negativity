package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class GroundSpoofData extends CheckData {

	public boolean wasAlert = false;
	
	public GroundSpoofData(NegativityPlayer np) {
		super(np);
	}
}
