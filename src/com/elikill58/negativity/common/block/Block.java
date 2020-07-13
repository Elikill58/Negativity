package com.elikill58.negativity.common.block;

import com.elikill58.negativity.common.item.Material;

public abstract class Block {

	public abstract Material getType();

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();
	
	public abstract Block getRelative(BlockFace blockFace);
}
