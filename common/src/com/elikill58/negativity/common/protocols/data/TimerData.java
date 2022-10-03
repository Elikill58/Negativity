package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class TimerData extends CheckData {
	
	public List<Integer> timerCount = new ArrayList<>();
	
	public TimerData(NegativityPlayer np) {
		super(np);
	}
}
