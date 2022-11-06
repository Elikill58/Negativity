package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.TimedHashMap;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockHashMap extends TimedHashMap<String, Block> {

	private static final long serialVersionUID = 1L;
	
	private World w;
	
	public BlockHashMap(World w) {
		this.w = w;
	}
	
	private String getKey(int x, int y, int z) {
		return x + "_" + y + "_" + z;
	}
	
	private String getKey(Vector v) {
		return v.getBlockX() + "_" + v.getBlockY() + "_" + v.getBlockZ();
	}

	public Block get(int x, int y, int z) {
		return super.computeIfAbsent(getKey(x, y, z), s -> w.getBlockAt0(x, y, z));
	}

	public Block get(Vector v) {
		return super.computeIfAbsent(getKey(v), s -> w.getBlockAt0(v.getBlockX(), v.getBlockY(), v.getBlockZ()));
	}
	
	public void set(int x, int y, int z, Block type) {
		super.put(getKey(x, y, z), type);
	}
	
	public void set(Vector v, Block type) {
		super.put(getKey(v), type);
	}
}
