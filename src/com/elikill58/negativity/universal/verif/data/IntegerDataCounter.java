package com.elikill58.negativity.universal.verif.data;

import org.json.simple.JSONObject;

public class IntegerDataCounter extends DataCounter<Integer> {

	public IntegerDataCounter(JSONObject json, String name) {
		super(json, name);
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

	@Override
	public boolean has() {
		return !list.isEmpty();
	}
}
