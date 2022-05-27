package com.elikill58.negativity.sponge7.impl.plugin;

import org.spongepowered.api.plugin.PluginContainer;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

public class SpongeExternalPlugin extends ExternalPlugin {

	private final PluginContainer pl;
	
	public SpongeExternalPlugin(PluginContainer pl) {
		this.pl = pl;
	}
	
	@Override
	public String getId() {
		return pl.getId();
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Object getDefault() {
		// Let's assume the instance exists
		// Let's assume the instance exists
		//noinspection OptionalGetWithoutIsPresent
		return pl.getInstance().get();
	}
	
	
}
