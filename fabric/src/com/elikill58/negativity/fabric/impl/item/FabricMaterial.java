package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Material;

import net.minecraft.item.Item;

public class FabricMaterial extends Material {

	private final Item type;
	
	public FabricMaterial(Item type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return type.isDamageable();
	}

	@Override
	public String getId() {
		return type.toString();
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public Object getDefault() {
		return type;
	}
}
