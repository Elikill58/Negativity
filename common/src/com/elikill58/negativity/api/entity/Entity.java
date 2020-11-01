package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.BlockRay.BlockRayBuilder;
import com.elikill58.negativity.api.ray.BlockRayResult;

public abstract class Entity extends CommandSender {

	public abstract boolean isOnGround();
	public abstract boolean isOp();

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
		BlockRayResult result = new BlockRayBuilder(getLocation(), this).maxTest(maxDistance).ignoreAir(true).build().compile();
		Block b = result.getBlock();
		return b == null ? new ArrayList<>() : Arrays.asList(b);
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
