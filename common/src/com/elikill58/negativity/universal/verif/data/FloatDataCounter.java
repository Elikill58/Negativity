package com.elikill58.negativity.universal.verif.data;

import java.util.Comparator;

public class FloatDataCounter extends DataCounter<Float> {

	@Override
	public Float getTotal() {
		float d = 0;
		for(Float temp : list)
			d += temp;
		return d;
	}

	@Override
	public Float getMin() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.naturalOrder());
		return list.get(0);
	}

	@Override
	public Float getMax() {
		if(list.isEmpty())
			return null;
		list.sort(Comparator.reverseOrder());
		return list.get(0);
	}

	@Override
	public Float getAverage() {
		if(list.isEmpty())
			return 0f;
		float d = 0;
		for(Float temp : list)
			d += temp;
		return d / list.size();
	}
}
