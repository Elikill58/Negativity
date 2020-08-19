package com.elikill58.negativity.sponge.impl.item;

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
