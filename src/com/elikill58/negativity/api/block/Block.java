package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.google.common.base.Preconditions;

public abstract class Block extends NegativityObject {

	public abstract Location getLocation();
	
	public abstract Material getType();

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();
	
	public abstract Block getRelative(BlockFace blockFace);

	public abstract boolean isLiquid();

	public abstract void setType(Material type);
	
	public abstract boolean isWaterLogged();
	
	@Override
	public boolean equals(Object obj) {
		Preconditions.checkNotNull(obj);
		if(!(obj instanceof Block))
			return false;
		Block b = (Block) obj;
		return b.getLocation().equals(getLocation()) && getType().equals(b.getType());
	}
	
	@Override
	public String toString() {
		return "Block{type=" + getType().getId() + ",x=" + getX() + ",y=" + getY() + ",z=" + getZ() + "}";
	}
}
