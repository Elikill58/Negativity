package com.elikill58.negativity.sponge7.impl.inventory;

import org.spongepowered.api.item.inventory.Carrier;

import com.elikill58.negativity.api.inventory.InventoryHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;

public class SpongeInventoryHolder extends InventoryHolder {

	private final Carrier holder;
	
	public SpongeInventoryHolder(Carrier holder) {
		this.holder = holder;
	}

	public Carrier getHolder() {
		return holder;
	}

	@Override
	public PlatformHolder getBasicHolder() {
		return null;
	}

}
