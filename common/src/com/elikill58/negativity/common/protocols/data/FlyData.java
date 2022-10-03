package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class FlyData extends CheckData {

	public double groundWarn = 0;
	public int notMovingY = 0, y0times = 0, nbAirBelow = 0;
	public boolean boatFalling = false, wasOnGround = false;
	public List<Double> flyMove = new ArrayList<>();
	
	public FlyData(NegativityPlayer np) {
		super(np);
	}
}
