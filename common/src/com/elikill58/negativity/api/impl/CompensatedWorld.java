package com.elikill58.negativity.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.chunks.Chunk;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.block.CompensatedBlock;
import com.elikill58.negativity.api.impl.block.EmptyBlock;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class CompensatedWorld extends World {

	protected final EmptyBlock EMPTY = new EmptyBlock(this);
	protected final Player p;
	protected List<Entity> entities = new ArrayList<>();
	protected Long2ObjectMap<Chunk> chunks = new Long2ObjectArrayMap<>();
	protected String name;
	
	public CompensatedWorld(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	private Chunk getChunkAt(int chunkX, int chunkZ) {
		return chunks.computeIfAbsent(getKey(chunkX, chunkZ), (a) -> new Chunk(chunkX, chunkZ));
	}
	
	private long getKey(int chunkX, int chunkZ) {
		return chunkX & 0xFFFFFFFFL | (chunkZ & 0xFFFFFFFFL) << 32L;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Block getBlockAt0(Location loc) {
		return getBlockAt0(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		return new CompensatedBlock(new Location(this, x, y, z), getChunkAt(x / 16, z / 16).get(x % 16, y, z % 16));
	}
	
	public void setBlock(Material type, Location loc) {
		setBlock(type, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public void setBlock(Material type, int x, int y, int z) {
		getChunkAt(x / 16, z / 16).set(x % 16, y, z % 16, type);
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(int id) {
		entities.removeIf(et -> et.isSameId(id));
	}
	
	public Long2ObjectMap<Chunk> getChunks() {
		return chunks;
	}

	@Override
	public List<Entity> getEntities() {
		return entities;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.PEACEFUL;
	}

	@Override
	public int getMaxHeight() {
		return 64;
	}

	@Override
	public int getMinHeight() {
		return 0;
	}

	@Override
	public boolean isPVP() {
		return false;
	}
	
	@Override
	public Object getDefault() {
		return this;
	}
}
