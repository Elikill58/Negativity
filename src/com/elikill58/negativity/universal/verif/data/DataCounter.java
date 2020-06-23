package com.elikill58.negativity.universal.verif.data;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCounter<T> {

	protected final List<T> list = new ArrayList<>();
	
	public List<T> getList(){
		return list;
	}
	
	public int getSize() {
		return list.size();
	}
	
	public void add(T value) {
		list.add(value);
	}

	public abstract T getMin();
	public abstract T getMax();
	
	public abstract T getAverage();

	public boolean has() {
		return !list.isEmpty();
	}
}
