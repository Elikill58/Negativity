package com.elikill58.negativity.universal.verif.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public abstract class DataCounter<T> {

	protected final List<T> list;
	protected final String name;
	
	@SuppressWarnings("unchecked")
	public DataCounter(JSONObject json, String name) {
		this.list = json == null ? new ArrayList<>() : (List<T>) json.getOrDefault("data", new ArrayList<>());
		this.name = name;
	}
	
	public abstract void add(T value);

	public abstract T getAverage();

	public abstract boolean has();

	@SuppressWarnings("unchecked")
	public JSONObject print() {
		JSONObject json = new JSONObject();
		json.put("type", this.name);
		json.put("data", this.list);
		return json;
	}
}
