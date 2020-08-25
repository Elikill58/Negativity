package com.elikill58.negativity.api;

import java.util.HashMap;

public class Content<T> {
	
	/**
	 * The saved content. Please, to use directly this map
	 */
	private final HashMap<String, HashMap<String, T>> mainContent = new HashMap<>();
	
	/**
	 * Obtain the saved value
	 * 
	 * @param type where the value is
	 * @param valueName the value name
	 * @param defaultValue returned when type or valueName not saved / don't exist
	 * @return
	 */
	public T get(String type, String valueName, T defaultValue) {
		HashMap<String, T> hashContent = mainContent.get(type);
		if(hashContent == null)
			return defaultValue;
		return hashContent.getOrDefault(valueName, defaultValue);
	}
	
	/**
	 * Edit the value :
	 * type > valueName:value
	 * 
	 * @param type where the value will be
	 * @param valueName the value key
	 * @param value the saved value
	 */
	public void set(String type, String valueName, T value) {
		mainContent.computeIfAbsent(type, (typee) -> new HashMap<>()).put(valueName, value);
	}
	
	/**
	 * Remove value for the specified type
	 * 
	 * @param type where the value to remove is
	 * @param valueName the value to remove
	 */
	public void remove(String type, String valueName) {
		try {
			mainContent.get(type).remove(valueName);
		} catch (NullPointerException e) {
			// ignore because the type is not present, so we have already remove it
		}
	}
}
