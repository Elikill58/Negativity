package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockPosition {

	private int x, y, z;
	
	public BlockPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockPosition(Location loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public BlockPosition(Vector loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public Location toLocation(World w) {
		return new Location(w, x, y, z);
	}
}
