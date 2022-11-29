package com.elikill58.negativity.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.block.CompensatedBlock;
import com.elikill58.negativity.api.impl.block.EmptyBlock;
import com.elikill58.negativity.api.impl.item.CompensatedMaterial;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class CompensatedWorld extends World {

	protected final EmptyBlock EMPTY = new EmptyBlock(this);
	protected final Player p;
	protected List<Entity> entities = new ArrayList<>();
	protected HashMap<String, Block> blocks = new HashMap<>();
	protected String name;
	
	public CompensatedWorld(Player p) {
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	private String getKey(int x, int y, int z) {
		return x + "_" + y + "_" + z;
	}
	
	private String getKey(Vector v) {
		return getKey(v.getBlockX(), v.getBlockY(), v.getBlockZ());
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
		return blocks.getOrDefault(getKey(loc.toBlockVector()), EMPTY);
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		return blocks.getOrDefault(getKey(x, y, z), EMPTY);
	}
	
	public Block createDefault(int x, int y, int z) {
		return new CompensatedBlock(new Location(this, x, y, z), new CompensatedMaterial("air"));
	}
	
	public void setBlock(Material type, Location loc) {
		blocks.put(getKey(loc.toBlockVector()), new CompensatedBlock(loc, type));
	}
	
	public void setBlock(Material type, int x, int y, int z) {
		blocks.put(getKey(x, y, z), new CompensatedBlock(new Location(this, x, y, z), type));
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(int id) {
		entities.removeIf(et -> et.isSameId(id));
	}
	
	public HashMap<String, Block> getBlocks() {
		return blocks;
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
