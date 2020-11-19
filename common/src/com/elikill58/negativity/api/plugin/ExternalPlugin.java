package com.elikill58.negativity.api.plugin;

import com.elikill58.negativity.api.NegativityObject;

public abstract class ExternalPlugin extends NegativityObject {
	
	public abstract String getId();
	
	/**
	 * Check if the plugin is enabled
	 *
	 * @return true if the plugin is enabled
	 */
	public abstract boolean isEnabled();
}
