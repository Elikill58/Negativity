package com.elikill58.negativity.api.plugin;

import com.elikill58.negativity.api.NegativityObject;

public abstract class ExternalPlugin implements NegativityObject {
	
	public abstract String getId();
	
	/**
	 * Check if the plugin is enabled
	 *
	 * @return true if the plugin is enabled
	 */
	public abstract boolean isEnabled();
	
	public abstract String getVersion();
}
