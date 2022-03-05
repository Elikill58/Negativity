package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.block.BlockRay;
import com.elikill58.negativity.api.ray.block.BlockRayBuilder;
import com.elikill58.negativity.api.ray.block.BlockRayResult;
import com.elikill58.negativity.universal.Adapter;

public abstract class AbstractEntity implements Entity {
	
	private Vector velocity = null;
	
	@Override
	public List<Block> getTargetBlock(int maxDistance) {
		BlockRay ray = (this instanceof Player ? new BlockRayBuilder((Player) this) : new BlockRayBuilder(getLocation(), this))
				.maxDistance(maxDistance).ignoreAir(true).build();
		BlockRayResult result = ray.compile();
		if(!result.getRayResult().isFounded()) {
			Adapter.getAdapter().debug("Begin: " + getLocation() + ", vec: " + ray.getVector().toString());
			Adapter.getAdapter().debug("Tested locs: " + result.getAllTestedLoc().toString());
			return new ArrayList<>();
		}
		return Arrays.asList(result.getBlock());
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
