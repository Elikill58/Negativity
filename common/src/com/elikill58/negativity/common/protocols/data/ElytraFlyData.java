package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class ElytraFlyData extends CheckData {

	public boolean useBypass = false;
	
	public ElytraFlyData(NegativityPlayer np) {
		super(np);
	}
}
