package com.elikill58.negativity.api;

import com.elikill58.negativity.api.location.World;

public abstract class NegativityObject {
	
	/**
	 * Get default object which is abstracted by Negativity structure
	 * For example, when we use {@link World}, beside there is the Spigot/Sponge... world.
	 * We can get the default platform object, in the example, the platform world.
	 * 
	 * @return default object
	 */
	public abstract Object getDefault();
}
