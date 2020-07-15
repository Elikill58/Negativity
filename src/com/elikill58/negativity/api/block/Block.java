package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;

public abstract class Block {

	public abstract Location getLocation();
	
	public abstract Material getType();

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();
	
	public abstract Block getRelative(BlockFace blockFace);

	public abstract boolean isLiquid();

	public abstract void setType(Material type);
}
