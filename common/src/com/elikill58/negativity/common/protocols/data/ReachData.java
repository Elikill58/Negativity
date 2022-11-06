package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.protocols.CheckData;

public class ReachData extends CheckData {

	public Location cibleLocation;
	public Entity cible;
	
	public ReachData(NegativityPlayer np) {
		super(np);
	}
	
	public void reset() {
		cible = null;
		cibleLocation = null;
	}
}
