package com.elikill58.negativity.api.impl;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockHashMap;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.block.CompensatedBlock;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;

public class CompensatedWorld extends World {

	private final Player p;
	private List<Entity> entities = new ArrayList<>();
	private BlockHashMap blocks;
	private String name;
	
	public CompensatedWorld(Player p) {
		this.p = p;
		this.blocks = new BlockHashMap(this);
		this.blocks.setDefaultType(Materials.AIR);
	}
	
	public Player getPlayer() {
		return p;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Block getBlockAt0(Location loc) {
		return blocks.get(loc.toBlockVector());
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		return blocks.get(x, y, z);
	}
	
	public void setBlock(Material type, Location loc) {
		blocks.set(loc.toBlockVector(), new CompensatedBlock(loc, type));
	}
	
	public void setBlock(Material type, int x, int y, int z) {
		blocks.set(x, y, z, new CompensatedBlock(new Location(this, x, y, z), type));
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
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
