package com.elikill58.negativity.universal.verif.data;

public class DoubleDataCounter extends DataCounter<Double> {

	public DoubleDataCounter(String name, String display) {
		super(name, display);
	}
	
	@Override
	public void add(Double d) {
		list.add(d);
	}

	@Override
	public Double getAverage() {
		if(list.isEmpty())
			return 0.0;
		double d = 0;
		for(Double temp : list)
			d += temp;
		return d / list.size();
	}

	@Override
	public boolean has() {
		return !list.isEmpty();
	}
}
