package com.elikill58.negativity.api.location;

import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;

public abstract class World {

	public abstract String getName();

	public abstract Block getBlockAt(int x, int y, int z);
	public abstract Block getBlockAt(Location loc);
	
	public abstract List<Entity> getEntities();
	
	public abstract Object getDefaultWorld();

	public abstract Difficulty getDifficulty();
}
