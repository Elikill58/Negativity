package com.elikill58.negativity.universal.verif.data;

public class FloatDataCounter extends DataCounter<Float> {

	@Override
	protected void internalAdd(Float value) {
		total += value;
	}

	@Override
	protected void internalManageMinMax(Float value) {
		if(value < min)
			min = value;
		if(value > max)
			max = value;
	}

	@Override
	public Float getAverage() {
		return total / amount;
	}
}
