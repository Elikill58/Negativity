package com.elikill58.negativity.spigot.item;

import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.universal.adapter.Adapter;

public class SpigotItemStack extends ItemStack {

	private final org.bukkit.inventory.ItemStack item;
	
	public SpigotItemStack(org.bukkit.inventory.ItemStack item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return item.getAmount();
	}

	@Override
	public Material getType() {
		return Adapter.getAdapter().getItemRegistrar().get(item.getType().name());
	}

	@Override
	public String getName() {
		return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
	}
}
