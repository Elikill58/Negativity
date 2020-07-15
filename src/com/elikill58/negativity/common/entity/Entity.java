package com.elikill58.negativity.common.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.elikill58.negativity.common.block.Block;
import com.elikill58.negativity.common.block.BlockIterator;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.location.Location;

public abstract class Entity {

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
	
	public abstract EntityType getType();
	
	public abstract int getEntityId();
	
	public abstract Object getDefaultEntity();
}
