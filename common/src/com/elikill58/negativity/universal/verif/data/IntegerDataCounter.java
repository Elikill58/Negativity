package com.elikill58.negativity.universal.verif.data;

public class IntegerDataCounter extends DataCounter<Integer> {

	@Override
	protected void internalAdd(Integer value) {
		total += value;
	}

	@Override
	protected void internalManageMinMax(Integer value) {
		if(value < min)
			min = value;
		if(value > max)
			max = value;
	}

	@Override
	public Integer getAverage() {
		return total / amount;
	}
}
