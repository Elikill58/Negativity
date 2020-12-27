package com.elikill58.negativity.sponge8.impl.item;

import java.util.Optional;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge8.utils.Utils;

public class SpongeMaterial extends Material {

	private final ItemType type;
	
	public SpongeMaterial(ItemType type) {
		this.type = type;
	}
	
	@Override
	public boolean isSolid() {
		Optional<BlockType> blockType = type.getBlock();
		return !blockType.isPresent() || blockType.get().getDefaultState().require(Keys.IS_SOLID);
	}

	@Override
	public String getId() {
		return Utils.getKey(type).asString();
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
