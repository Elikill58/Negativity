package com.elikill58.negativity.common.protocols.data;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.protocols.CheckData;

public class JesusData extends CheckData {

	public boolean wasGround = false, yDifState = false;
	private double yDifValueTrue = 0, yDifValueFalse = 0;
	public double bufferDistanceIn = 0;
	
	public JesusData(NegativityPlayer np) {
		super(np);
	}
	
	public double getYDiff() {
		return yDifState ? yDifValueTrue : yDifValueFalse;
	}
	
	public void applyYDiff(double d) {
		if(yDifState) {
			yDifValueTrue = d;
		} else {
			yDifValueFalse = d;
		}
		yDifState = !yDifState;
	}
	
	public void reduceBufferDistanceIn() {
		bufferDistanceIn -= 0.5;
		if(bufferDistanceIn < 0)
			bufferDistanceIn = 0;
	}
}
