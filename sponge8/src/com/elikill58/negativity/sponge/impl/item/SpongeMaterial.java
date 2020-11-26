package com.elikill58.negativity.sponge.impl.item;

import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.item.Material;

public class SpongeMaterial extends Material {

	private final ItemType type;
	
	public SpongeMaterial(ItemType type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return true; // TODO find how to implement this
	}

	@Override
	public String getId() {
		return type.key().asString();
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
