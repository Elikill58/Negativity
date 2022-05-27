package com.elikill58.negativity.sponge.impl.plugin;

import org.spongepowered.plugin.PluginContainer;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

public class SpongeExternalPlugin extends ExternalPlugin {

	private final PluginContainer pl;
	
	public SpongeExternalPlugin(PluginContainer pl) {
		this.pl = pl;
	}
	
	@Override
	public String getId() {
		return pl.metadata().id();
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Object getDefault() {
		return pl.instance();
	}
	
	
}
