package com.elikill58.negativity.sponge.impl.block;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge.utils.Utils;

public class SpongeBlockMaterial extends Material {
	
	private final BlockType blockType;
	
	public SpongeBlockMaterial(BlockType blockType) {
		this.blockType = blockType;
	}
	
	@Override
	public BlockType getDefault() {
		return this.blockType;
	}
	
	@Override
	public boolean isSolid() {
		return this.blockType.defaultState().require(Keys.IS_SOLID);
	}
	
	@Override
	public boolean isTransparent() {
		return false;
	}
	
	@Override
	public String getId() {
		return Utils.getKey(this.blockType).asString();
	}
}
