package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class FastPlaceData extends CheckData {
	
	public List<Integer> times = new ArrayList<>();
	public int lastTick;
	
	public FastPlaceData(NegativityPlayer np) {
		super(np);
	}
}
