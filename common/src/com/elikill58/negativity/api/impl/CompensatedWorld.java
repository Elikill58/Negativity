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
import com.elikill58.negativity.universal.Version;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class CompensatedWorld extends World {

	private static final int MIN_HEIGHT, MAX_HEIGHT;
	
	static {
		if(Version.getVersion().isNewerOrEquals(Version.V1_17)) {
			MIN_HEIGHT = -64;
			MAX_HEIGHT = 320;
		} else {
			MIN_HEIGHT = 0;
			MAX_HEIGHT = 256;
		}
	}
	
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
	
	public void setChunk(Chunk c) {
		long key = getKey(c.getX(), c.getZ());
		Chunk actual = chunks.get(key);
		if(actual == null)
			chunks.put(key, c);
		else
			actual.addChunk(c);
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
		return MAX_HEIGHT;
	}

	@Override
	public int getMinHeight() {
		return MIN_HEIGHT;
	}

	@Override
	public boolean isPVP() {
		return true;
	}
	
	@Override
	public Object getDefault() {
		return this;
	}
}
