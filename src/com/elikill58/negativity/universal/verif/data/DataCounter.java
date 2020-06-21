package com.elikill58.negativity.universal.verif.data;

import java.util.ArrayList;
import java.util.List;

public abstract class DataCounter<T> {

	protected final List<T> list = new ArrayList<>();
	protected final String name, displayName;
	
	public DataCounter(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplay() {
		return displayName;
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
	
	public String print() {
		return "[{\"data\":" + this.list.toString() + ",\"display\":\"" + this.displayName + "\",\"type\":\"" + this.name + "\"}]";
	}
}
