package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.protocols.CheckData;

public class SpiderData extends CheckData {

	public List<Double> lastY = new ArrayList<>();
	public Location lastSpiderLoc = null;
	public double lastDistance = 0;
	public int spiderSameDist = 0;
	
	public SpiderData(NegativityPlayer np) {
		super(np);
	}
}
