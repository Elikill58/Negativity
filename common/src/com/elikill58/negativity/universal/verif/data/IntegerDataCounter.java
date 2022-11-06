package com.elikill58.negativity.universal.verif.data;

import java.util.Comparator;

public class IntegerDataCounter extends DataCounter<Integer> {

	@Override
	public Integer getTotal() {
		int d = 0;
		for(Integer temp : list)
			d += temp;
		return d;
	}

	@Override
	public Integer getMin() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.naturalOrder());
		return list.get(0);
	}

	@Override
	public Integer getMax() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.reverseOrder());
		return list.get(0);
	}

	@Override
	public Integer getAverage() {
		if(list.isEmpty())
			return 0;
		double d = 0;
		for(Integer temp : list)
			d += temp;
		return (int) (d / list.size());
	}
}
