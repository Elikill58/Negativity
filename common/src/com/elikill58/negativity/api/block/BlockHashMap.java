package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.TimedHashMap;
import com.elikill58.negativity.api.impl.block.CompensatedBlock;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockHashMap extends TimedHashMap<String, Block> {

	private static final long serialVersionUID = 1L;
	
	private World w;
	private Material defType;
	
	public BlockHashMap(World w) {
		this.w = w;
	}
	
	public void setDefaultType(Material defType) {
		this.defType = defType;
	}
	
	private String getKey(int x, int y, int z) {
		return x + "_" + y + "_" + z;
	}
	
	private String getKey(Vector v) {
		return v.getBlockX() + "_" + v.getBlockY() + "_" + v.getBlockZ();
	}

	public Block get(int x, int y, int z) {
		return super.computeIfAbsent(getKey(x, y, z), s -> getDefault(x, y, z));
	}

	public Block get(Vector v) {
		return super.computeIfAbsent(getKey(v), s -> getDefault(v.getBlockX(), v.getBlockY(), v.getBlockZ()));
	}
	
	private Block getDefault(int x, int y, int z) {
		return defType == null ? w.getBlockAt0(x, y, z) : new CompensatedBlock(new Location(w, x, y, z), defType);
	}
	
	public void set(int x, int y, int z, Block type) {
		super.put(getKey(x, y, z), type);
	}
	
	public void set(Vector v, Block type) {
		super.put(getKey(v), type);
	}
}
