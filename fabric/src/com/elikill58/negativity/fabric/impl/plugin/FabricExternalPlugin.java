package com.elikill58.negativity.fabric.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

import net.minecraft.resource.Resource;

public class FabricExternalPlugin extends ExternalPlugin {

	private final Resource pl;
	
	public FabricExternalPlugin(Resource pl) {
		this.pl = pl;
	}
	
	@Override
	public String getId() {
		return pl.getId().getNamespace();
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
		return pl;
	}
	
	
}
