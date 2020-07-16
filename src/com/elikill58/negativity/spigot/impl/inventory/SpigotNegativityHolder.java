package com.elikill58.negativity.spigot.impl.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.elikill58.negativity.api.inventory.PlatformHolder;

public class SpigotNegativityHolder extends PlatformHolder implements InventoryHolder {

	private final PlatformHolder holder;
	
	public SpigotNegativityHolder(PlatformHolder holder) {
		this.holder = holder;
	}

	@Override
	public PlatformHolder getBasicHolder() {
		return holder;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

}
