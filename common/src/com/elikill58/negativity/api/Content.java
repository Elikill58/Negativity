package com.elikill58.negativity.api;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.elikill58.negativity.universal.detections.keys.IDetectionKeys;

public class Content<T> {
	
	/**
	 * The saved content. Please, don't use directly this map
	 */
	private final ConcurrentHashMap<IDetectionKeys<?>, HashMap<String, T>> mainContent = new ConcurrentHashMap<>();
	
	/**
	 * Obtain the saved value
	 * 
	 * @param type where the value is
	 * @param valueName the value name
	 * @param defaultValue returned when type or valueName not saved / don't exist
	 * @return the needed value according to the type and the valueName
	 */
	public T get(IDetectionKeys<?> type, String valueName, T defaultValue) {
		return mainContent.computeIfAbsent(type, (typee) -> new HashMap<>()).computeIfAbsent(valueName, (a) -> defaultValue);
	}
	
	/**
	 * Edit the value :
	 * type > valueName:value
	 * 
	 * @param type where the value will be
	 * @param valueName the value key
	 * @param value the saved value
	 */
	public void set(IDetectionKeys<?> type, String valueName, T value) {
		mainContent.computeIfAbsent(type, (typee) -> new HashMap<>()).put(valueName, value);
	}
	
	/**
	 * Remove value for the specified type
	 * 
	 * @param type where the value to remove is
	 * @param valueName the value to remove
	 * @return the removed value or null
	 */
	public T remove(IDetectionKeys<?> type, String valueName) {
		try {
			return mainContent.get(type).remove(valueName);
		} catch (NullPointerException e) {
			// ignore because the type is not present, so we have already remove it
		}
		return null;
	}
	
	/**
	 * Check if the given value is set or not
	 * 
	 * @param type the key of the detection
	 * @param valueName the name of the value
	 * @return true if the value is present
	 */
	public boolean has(IDetectionKeys<?> type, String valueName) {
		return mainContent.containsKey(type) && mainContent.get(type).containsKey(valueName);
	}
	
	public HashMap<String, T> getAllContent(IDetectionKeys<?> type){
		return mainContent.computeIfAbsent(type, (typee) -> new HashMap<>());
	}
}
