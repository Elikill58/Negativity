package com.elikill58.negativity.api;

import java.util.HashMap;

public class Content<T> {
	
	private final HashMap<String, HashMap<String, T>> mainContent = new HashMap<>();
	
	public T get(String type, String valueName, T defaultValue) {
		HashMap<String, T> hashContent = mainContent.get(type);
		if(hashContent == null)
			return defaultValue;
		return hashContent.getOrDefault(valueName, defaultValue);
	}
	
	public void set(String type, String valueName, T value) {
		mainContent.computeIfAbsent(type, (typee) -> new HashMap<>());
		mainContent.get(type).put(valueName, value);
	}
	
	public void remove(String type, String valueName) {
		try {
			mainContent.get(type).remove(valueName);
		} catch (NullPointerException e) {
			// ignore because the type is not present, so we have already remove it
		}
	}
}
