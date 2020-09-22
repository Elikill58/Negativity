package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockIterator;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;

public abstract class Entity extends CommandSender {

	public abstract boolean isOnGround();
	public abstract boolean isOp();

	public List<Block> getTargetBlock(int maxDistance) {
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<>();
		Iterator<Block> itr = new BlockIterator(this, maxDistance);
		while (itr.hasNext()) {
			Block block = (Block) itr.next();
			blocks.add(block);
			Material material = block.getType();
			if (material.isTransparent()) {
				break;
			}
		}
		return blocks;
	}
	
	public abstract Location getLocation();
	
	public abstract double getEyeHeight();
	
	public abstract Location getEyeLocation();
	
	public abstract Vector getRotation();
	
	public abstract EntityType getType();
	
	public abstract int getEntityId();
	
	@Override
	public String toString() {
		return "Entity{type=" + getType().name() + ",x=" + getLocation().getX() + ",y=" + getLocation().getY() + ",z=" + getLocation().getZ() + "}";
	}
}
