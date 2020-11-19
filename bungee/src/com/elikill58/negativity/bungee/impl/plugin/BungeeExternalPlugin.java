package com.elikill58.negativity.bungee.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeExternalPlugin extends ExternalPlugin {

	private final Plugin pl;
	
	public BungeeExternalPlugin(Plugin pl) {
		this.pl = pl;
	}
	
	@Override
	public String getId() {
		return pl.getDescription().getName();
	}
	
	@Override
	public boolean isEnabled() {
		return pl != null;
	}

	@Override
	public Object getDefault() {
		return pl;
	}
	
}
