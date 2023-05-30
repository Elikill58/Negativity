package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class NukerData extends CheckData {

	public int oldTicks = 0;
	public List<Integer> ticks = new ArrayList<>();
	
	public NukerData(NegativityPlayer np) {
		super(np);
	}
}
