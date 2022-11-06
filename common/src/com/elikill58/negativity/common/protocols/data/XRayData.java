package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.protocols.CheckData;

public class XRayData extends CheckData {

	public Location miningLoc;
	public int miningOre = 0;
	public long mining = 0;
	
	public XRayData(NegativityPlayer np) {
		super(np);
	}
}
