package com.elikill58.negativity.spigot.item;

import java.util.HashMap;

import com.elikill58.negativity.common.item.ItemRegistrar;
import com.elikill58.negativity.common.item.Material;

public class SpigotItemRegistrar extends ItemRegistrar {

	private final HashMap<String, Material> cache = new HashMap<>();

	@Override
	public Material get(String id) {
		return cache.computeIfAbsent(id, key -> new SpigotMaterial(org.bukkit.Material.matchMaterial(id)));
	}

}
