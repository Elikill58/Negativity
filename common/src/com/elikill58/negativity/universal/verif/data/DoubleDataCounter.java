package com.elikill58.negativity.universal.verif.data;

import java.util.Comparator;

public class DoubleDataCounter extends DataCounter<Double> {

	@Override
	public Double getTotal() {
		double d = 0;
		for(Double temp : list)
			d += temp;
		return d;
	}

	@Override
	public Double getMin() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.naturalOrder());
		return list.get(0);
	}

	@Override
	public Double getMax() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.reverseOrder());
		return list.get(0);
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
}
