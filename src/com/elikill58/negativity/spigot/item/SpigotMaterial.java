package com.elikill58.negativity.spigot.item;

import com.elikill58.negativity.common.item.Material;

public class SpigotMaterial implements Material {

	private final org.bukkit.Material type;
	
	public SpigotMaterial(org.bukkit.Material type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return type.isSolid();
	}

	@Override
	public String getId() {
		return type.name();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isTransparent() {
		return type.isTransparent();
	}

}
