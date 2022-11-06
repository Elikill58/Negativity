package com.elikill58.negativity.universal.verif.data;

import java.util.Comparator;

public class LongDataCounter extends DataCounter<Long> {

	@Override
	public Long getTotal() {
		long d = 0;
		for(Long temp : list)
			d += temp;
		return d;
	}
	
	@Override
	public Long getMin() {
		if(list.isEmpty())
			return 0l;
		list.sort(Comparator.naturalOrder());
		return list.get(0);
	}

	@Override
	public Long getMax() {
		if(list.isEmpty())
			return 0l;
		list.sort(Comparator.reverseOrder());
		return list.get(0);
	}

	@Override
	public Long getAverage() {
		if(list.isEmpty())
			return 0l;
		long d = 0l;
		for(Long temp : list)
			d += temp;
		return d / list.size();
	}
}
