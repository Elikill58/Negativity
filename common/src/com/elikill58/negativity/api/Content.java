package com.elikill58.negativity.api;

import java.util.HashMap;

import com.elikill58.negativity.universal.CheatKeys;

public class Content<T> {
	
	/**
	 * The saved content. Please, to use directly this map
	 */
	private final HashMap<CheatKeys, HashMap<String, T>> mainContent = new HashMap<>();
	
	/**
	 * Obtain the saved value
	 * 
	 * @param type where the value is (we suggest you to use {@link CheatKeys}}
	 * @param valueName the value name
	 * @param defaultValue returned when type or valueName not saved / don't exist
	 * @return the needed value according to the type and the valueName
	 */
	public T get(CheatKeys type, String valueName, T defaultValue) {
		return mainContent.computeIfAbsent(type, (typee) -> new HashMap<>()).computeIfAbsent(valueName, (a) -> defaultValue);
	}
	
	/**
	 * Edit the value :
	 * type > valueName:value
	 * 
	 * @param type where the value will be (we suggest you to use {@link CheatKeys}}
	 * @param valueName the value key
	 * @param value the saved value
	 */
	public void set(CheatKeys type, String valueName, T value) {
		mainContent.computeIfAbsent(type, (typee) -> new HashMap<>()).put(valueName, value);
	}
	
	/**
	 * Remove value for the specified type
	 * 
	 * @param type where the value to remove is (we suggest you to use {@link CheatKeys}}
	 * @param valueName the value to remove
	 */
	public void remove(CheatKeys type, String valueName) {
		try {
			mainContent.get(type).remove(valueName);
		} catch (NullPointerException e) {
			// ignore because the type is not present, so we have already remove it
		}
	}
}
