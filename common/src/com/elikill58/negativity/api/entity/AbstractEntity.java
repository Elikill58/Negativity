package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.ray.BlockRay;
import com.elikill58.negativity.api.ray.BlockRayResult;

public abstract class AbstractEntity implements Entity {
	
	@Override
	public List<Block> getTargetBlock(int maxDistance) {
		/*if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<>();
		Iterator<Block> itr = new BlockIterator(this, maxDistance);
		while (itr.hasNext() && maxDistance > 0 && blocks.size() < 5) {
			maxDistance--;
			Block block = (Block) itr.next();
			Material material = block.getType();
			if (!material.isTransparent()) {
				blocks.add(block);
			}
		}*/
		BlockRayResult result;
		result = new BlockRay.BlockRayBuilder(getLocation(), this).maxTest(maxDistance).ignoreAir(true).build().compile();
		Block b = result.getBlock();
		return b == null ? new ArrayList<>() : Arrays.asList(b);
	}
	
	@Override
	public String toString() {
		return "Entity{type=" + getType().name() + ",x=" + getLocation().getX() + ",y=" + getLocation().getY() + ",z=" + getLocation().getZ() + "}";
	}
}
