package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class NoSlowDownData extends CheckData {

	public double eatingDistance = 0;
	public boolean onSoulSand = false;
	
	public NoSlowDownData(NegativityPlayer np) {
		super(np);
	}
}
