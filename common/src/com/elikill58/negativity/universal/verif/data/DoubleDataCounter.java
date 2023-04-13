package com.elikill58.negativity.universal.verif.data;

public class DoubleDataCounter extends DataCounter<Double> {

	@Override
	protected void internalAdd(Double value) {
		total += value;
	}

	@Override
	protected void internalManageMinMax(Double value) {
		if(value < min)
			min = value;
		if(value > max)
			max = value;
	}

	@Override
	public Double getAverage() {
		return total / amount;
	}
}
