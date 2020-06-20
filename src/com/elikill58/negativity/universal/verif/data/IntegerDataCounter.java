package com.elikill58.negativity.universal.verif.data;

public class IntegerDataCounter extends DataCounter<Integer> {

	public IntegerDataCounter(String name, String display) {
		super(name, display);
	}
	
	@Override
	public void add(Integer d) {
		list.add(d);
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
