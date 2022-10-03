package com.elikill58.negativity.minestom.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

import net.minestom.server.extensions.Extension;

public class MinestomExternalPlugin extends ExternalPlugin {

	private final Extension e;
	
	public MinestomExternalPlugin(Extension e) {
		this.e = e;
	}
	
	@Override
	public String getId() {
		return e.getOrigin().getName();
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Object getDefault() {
		return e;
	}
	
	
}
