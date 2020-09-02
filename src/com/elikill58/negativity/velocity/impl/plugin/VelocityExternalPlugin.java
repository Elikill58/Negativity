package com.elikill58.negativity.velocity.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.velocitypowered.api.plugin.PluginContainer;

public class VelocityExternalPlugin extends ExternalPlugin {

	private final PluginContainer pl;
	
	public VelocityExternalPlugin(PluginContainer pl) {
		this.pl = pl;
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
