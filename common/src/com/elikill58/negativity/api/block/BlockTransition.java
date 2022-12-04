package com.elikill58.negativity.api.block;

import com.elikill58.negativity.api.item.Material;

public class BlockTransition {

	private final long begin, end;
	private final Material old, next;
	private final int x, y, z;
	
	public BlockTransition(long begin, long end, Material old, Material next, int x, int y, int z) {
		this.begin = begin;
		this.end = end;
		this.old = old;
		this.next = next;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public long getBegin() {
		return begin;
	}
	
	public long getEnd() {
		return end;
	}
	
	public Material getNext() {
		return next;
	}
	
	public Material getOld() {
		return old;
	}
	
	public boolean expired() {
		return end < System.currentTimeMillis();
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
	
	public boolean concern(int x, int y, int z) {
		return getX() == x && getY() == y && getZ() == z;
	}
}
