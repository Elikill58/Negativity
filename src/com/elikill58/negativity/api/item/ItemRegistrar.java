package com.elikill58.negativity.api.item;

import com.elikill58.negativity.universal.Adapter;

public abstract class ItemRegistrar {
	
	/**
	 * Get material's type
	 * (Save in cache if found any of them)
	 * 
	 * @param id the main ID of the material
	 * @param alias All others names that the material can have, according to version
	 * @return the material
	 */
	public abstract Material get(String id, String... alias);
	
	/**
	 * Get item registry instance
	 * To get all material
	 * 
	 * @return item registrar of current adapter
	 */
	public static ItemRegistrar getInstance() {
		return Adapter.getAdapter().getItemRegistrar();
	}
}
