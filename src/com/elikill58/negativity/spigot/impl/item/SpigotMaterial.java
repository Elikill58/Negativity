package com.elikill58.negativity.spigot.impl.item;

import com.elikill58.negativity.api.item.Material;

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

	@Override
	public Object getDefaultMaterial() {
		return type;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Material))
			return false;
		Material to = (Material) obj;
		if(this.getId().equals(to.getId()) && this.isSolid() == to.isSolid() && this.isTransparent() == to.isTransparent())
			return true;
		return false;
	}
}
