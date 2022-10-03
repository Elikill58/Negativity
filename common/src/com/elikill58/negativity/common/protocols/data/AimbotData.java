package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class AimbotData extends CheckData {

	public List<Double> allPitchs = new ArrayList<Double>(Arrays.asList(0d, 0d, 0d, 0d, 0d, 0d, 0d));
	public List<Integer> allInvalidChanges = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0));
	public int ratioStreak = 0;
	public double lastDeltaPitchStreak = 0;
	
	public AimbotData(NegativityPlayer np) {
		super(np);
	}
}
