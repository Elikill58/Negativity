package com.elikill58.negativity.api.block.chunks;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class Chunk {

	private int x, z;
	private Long2ObjectMap<Material> types = new Long2ObjectArrayMap<>(); 
	
	public Chunk(int x, int z) {
		this.x = x;
		this.z = x;
		types.defaultReturnValue(Materials.AIR);
	}
	
	private int getKey(int x, int y, int z) {
		return (y & 0xF) << 8 | (z & 0xF) << 4 | x & 0xF;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public void set(int x, int y, int z, Material type) {
		types.put(getKey(x, y, z), type);
	}
	
	public Material get(int x, int y, int z) {
		return types.get(getKey(x, y, z));
	}
}
