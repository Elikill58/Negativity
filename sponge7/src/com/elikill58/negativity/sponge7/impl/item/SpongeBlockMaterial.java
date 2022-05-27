package com.elikill58.negativity.sponge7.impl.item;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.property.block.SolidCubeProperty;

import com.elikill58.negativity.api.item.Material;

public class SpongeBlockMaterial extends Material {

	private final BlockType type;
	
	public SpongeBlockMaterial(BlockType type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		return type.getProperty(SolidCubeProperty.class).get().getValue();
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
