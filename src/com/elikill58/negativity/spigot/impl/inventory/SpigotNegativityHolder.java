package com.elikill58.negativity.spigot.impl.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.common.inventory.PlatformHolder;

public class SpigotNegativityHolder extends PlatformHolder implements InventoryHolder {

	private final NegativityHolder holder;
	
	public SpigotNegativityHolder(NegativityHolder holder) {
		this.holder = holder;
	}

	public NegativityHolder getBasicHolder() {
		return holder;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

}
