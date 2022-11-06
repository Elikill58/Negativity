package com.elikill58.negativity.api.location;

import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;

public class CompensatedWorld extends World {

	@Override
	public Object getDefault() {
		return this;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Block getBlockAt0(Location loc) {
		return null;
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		return null;
	}

	@Override
	public List<Entity> getEntities() {
		return null;
	}

	@Override
	public Difficulty getDifficulty() {
		return null;
	}

	@Override
	public int getMaxHeight() {
		return 0;
	}

	@Override
	public int getMinHeight() {
		return 0;
	}

	@Override
	public boolean isPVP() {
		return false;
	}

}
