package com.elikill58.negativity.sponge7.impl.item;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.SolidCubeProperty;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.item.Material;

public class SpongeMaterial extends Material {

	private final ItemType type;
	
	public SpongeMaterial(ItemType type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return type.getBlock().orElse(BlockTypes.AIR).getProperty(SolidCubeProperty.class).get().getValue();
	}

	@Override
	public String getId() {
		return type.getId();
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
