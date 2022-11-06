package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class StepData extends CheckData {
	
	public List<Double> oldY = new ArrayList<>();
	
	public StepData(NegativityPlayer np) {
		super(np);
	}
}
