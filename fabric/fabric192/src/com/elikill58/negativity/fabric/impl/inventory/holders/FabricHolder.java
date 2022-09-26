package com.elikill58.negativity.fabric.impl.inventory.holders;

import com.elikill58.negativity.api.inventory.PlatformHolder;

import net.minecraft.screen.ScreenHandlerContext;

public class FabricHolder extends PlatformHolder {
	
	private ScreenHandlerContext context;
	
	public FabricHolder(ScreenHandlerContext context) {
		this.context = context;
	}
	
	public ScreenHandlerContext getContext() {
		return context;
	}
	
	@Override
	public PlatformHolder getBasicHolder() {
		return null;
	}
}
