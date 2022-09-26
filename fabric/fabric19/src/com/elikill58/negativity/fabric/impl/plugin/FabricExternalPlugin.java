package com.elikill58.negativity.fabric.impl.plugin;

import com.elikill58.negativity.api.plugin.ExternalPlugin;

import net.minecraft.resource.Resource;

public class FabricExternalPlugin extends ExternalPlugin {

	private final String id;
	private final Resource pl;
	
	public FabricExternalPlugin(String id, Resource pl) {
		this.id = id;
		this.pl = pl;
	}
	
	@Override
	public String getId() {
		return id;
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
