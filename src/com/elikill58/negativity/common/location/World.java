package com.elikill58.negativity.common.location;

import java.util.List;

import com.elikill58.negativity.common.block.Block;
import com.elikill58.negativity.common.entity.Entity;

public abstract class World {

	public abstract String getName();

	public abstract Block getBlockAt(int x, int y, int z);
	public abstract Block getBlockAt(Location loc);
	
	public abstract List<Entity> getEntities();
	
	public abstract Object getDefaultWorld();
}
