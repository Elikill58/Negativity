package com.elikill58.negativity.universal.setBack;

import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.universal.adapter.Adapter;

public class SetBackEntry {

	private String type, key, value;
	
	/**
	 * Create a new set back entry.
	 * 
	 * @param json the json object which contains all set back data
	 */
	@SuppressWarnings("unchecked")
	public SetBackEntry(JSONObject json) {
		this.type = json.get("type").toString();
		this.key = json.getOrDefault("key", "").toString();
		this.value = json.getOrDefault("value", "").toString();
	}

	/**
	 * Create a new set back entry.
	 * 
	 * @param s the string which contains all set back data
	 */
	public SetBackEntry(String s) {
		String[] content = s.split(":");
		if(content.length <= 2) {
			Adapter.getAdapter().getLogger().warn("Wrong configuration of set_back. " + s + " is not well configurated.");
		} else {
			this.type = content[0];
			this.key = content[1];
			this.value = s.replaceFirst(type + ":", "").replaceFirst(key + ":", "");
		}
	}

	/**
	 * Get the type of the set back.
	 * It correspond to the processor name
	 * 
	 * @return the processor name
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Get the main data of the set back
	 * 
	 * @return key of data
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Get the value of the data (without the key and the type)
	 * 
	 * @return the value of data
	 */
	public String getValue() {
		return value;
	}
}
