package com.elikill58.negativity.universal.verif.data;

public class LongDataCounter extends DataCounter<Long> {

	@Override
	protected void internalAdd(Long value) {
		total += value;
	}

	@Override
	protected void internalManageMinMax(Long value) {
		if(value < min)
			min = value;
		if(value > max)
			max = value;
	}

	@Override
	public Long getAverage() {
		return total / amount;
	}
}
