package com.elikill58.negativity.api.json.parser;

import java.util.List;
import java.util.Map;

/**
 * Container factory for creating containers for JSON object and JSON array.
 * 
 * @see com.elikill58.negativity.api.json.parser.JSONParser#parse(java.io.Reader, ContainerFactory)
 * 
 * @author FangYidong
 */
public interface ContainerFactory {
	/**
	 * @return A Map instance to store JSON object, or null if you want to use org.json.JSONObject.
	 */
	Map createObjectContainer();
	
	/**
	 * @return A List instance to store JSON array, or null if you want to use org.json.JSONArray.
	 */
	List creatArrayContainer();
}
