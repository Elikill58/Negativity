package com.elikill58.negativity.spigot.impl.item;

import java.util.HashMap;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;

public class SpigotItemRegistrar extends ItemRegistrar {

	private final HashMap<String, Material> cache = new HashMap<>();

	@Override
	public Material get(String id, String... alias) {
		return cache.computeIfAbsent(id, key -> new SpigotMaterial(getMaterial(key, alias)));
	}
	
	private org.bukkit.Material getMaterial(String id, String... alias){
		org.bukkit.Material type = org.bukkit.Material.getMaterial(id.toUpperCase());
		if(type != null)
			return type;
		for(String s : alias) {
			type = org.bukkit.Material.getMaterial(s.toUpperCase());
			if(type != null)
				return type;
		}
		return null;
	}
}
