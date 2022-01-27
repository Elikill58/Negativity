package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.BlockRay;
import com.elikill58.negativity.api.ray.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;

public abstract class AbstractEntity implements Entity {
	
	private Vector velocity = null;
	
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
		BlockRay ray = new BlockRay.BlockRayBuilder(getLocation(), this).maxDistance(maxDistance).ignoreEntity(true).ignoreAir(true).build();
		BlockRayResult result = ray.compile();
		if(!result.getRayResult().isFounded()) {
			Adapter.getAdapter().debug("Begin: " + getLocation() + ", vec: " + ray.getVector().toString());
			Adapter.getAdapter().debug("Tested locs: " + result.getAllTestedLoc().toString());
		}
		Block b = result.getBlock();
		return b == null ? new ArrayList<>() : Arrays.asList(b);
	}
	
	@Override
	public void applyTheoricVelocity() {
		this.velocity = getTheoricVelocity();
	}
	
	@Override
	public Vector getVelocity() {
		return velocity == null ? getTheoricVelocity() : velocity;
	}
	
	@Override
	public String toString() {
		return "Entity{type=" + getType().name() + ",location=" + getLocation() + "}";
	}
}
