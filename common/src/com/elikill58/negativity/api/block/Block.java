package com.elikill58.negativity.api.block;

import java.util.Objects;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;

public abstract class Block implements NegativityObject {

	public abstract Location getLocation();
	
	public abstract Material getType();

	public abstract int getX();
	public abstract int getY();
	public abstract int getZ();
	
	public abstract Block getRelative(BlockFace blockFace);

	public abstract boolean isLiquid();

	public abstract void setType(Material type);
	
	public abstract boolean isWaterLogged();

    public float getFriction() {
    	String id = getType().getId().toLowerCase();
        if (id.contains("slime"))
            return 0.8f;
        else if (id.contains("ice"))
            return 0.98f;
        return 0.6f;
    }
	
	@Override
	public boolean equals(Object obj) {
		Objects.requireNonNull(obj);
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
