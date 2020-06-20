package com.elikill58.negativity.universal.verif.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public abstract class DataCounter<T> {

	protected final List<T> list = new ArrayList<>();
	protected final String name, displayName;
	
	public DataCounter(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
	}
	
	public int getSize() {
		return list.size();
	}
	
	public abstract void add(T value);

	public abstract T getAverage();

	public boolean has() {
		return !list.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public JSONObject print() {
		JSONObject json = new JSONObject();
		json.put("type", this.name);
		json.put("display", this.displayName);
		json.put("data", this.list);
		return json;
	}
}
