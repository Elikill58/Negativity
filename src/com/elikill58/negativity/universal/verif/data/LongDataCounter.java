package com.elikill58.negativity.universal.verif.data;

public class LongDataCounter extends DataCounter<Long> {

	public LongDataCounter(String name, String display) {
		super(name, display);
	}
	
	@Override
	public void add(Long d) {
		list.add(d);
	}

	@Override
	public Long getAverage() {
		if(list.isEmpty())
			return 0l;
		double d = 0;
		for(Long temp : list)
			d += temp;
		return (long) (d / list.size());
	}

	@Override
	public boolean has() {
		return !list.isEmpty();
	}
}
