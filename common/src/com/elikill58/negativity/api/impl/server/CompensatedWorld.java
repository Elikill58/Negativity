package com.elikill58.negativity.api.impl.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockTransition;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.server.block.CompensatedBlock;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.Adapter;

public class CompensatedWorld extends World {

	protected final Player p;
	protected World serverWorld;
	protected List<BlockTransition> transitions = Collections.synchronizedList(new ArrayList<>());
	
	public CompensatedWorld(Player p) {
		this.p = p;
	}
	
	public CompensatedWorld(Player p, World w) {
		this.p = p;
		this.serverWorld = w;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	protected World getServerWorld() {
		if(serverWorld == null)
			serverWorld = Adapter.getAdapter().getServerWorld(getPlayer());
		return serverWorld;
	}

	@Override
	public String getName() {
		return getServerWorld().getName();
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		transitions.removeIf(BlockTransition::expired);
		for(BlockTransition t : new ArrayList<>(transitions)) {
			if(t.concern(x, y, z)) {
				return new CompensatedBlock(new Location(this, x, y, z), t.getOld());
			}
		}
		return getServerWorld().getBlockAt(x, y, z);
	}

	public void addTimingBlock(long expireTime, Material next, int x, int y, int z) {
		long time = System.currentTimeMillis();
		transitions.add(new BlockTransition(time, time + expireTime, getServerWorld().getBlockAt0(x, y, z).getType(), next, x, y, z));
	}

	@Override
	public List<Entity> getEntities() {
		return getServerWorld().getEntities();
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		return getServerWorld().getEntityById(id);
	}
	
	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		return getServerWorld().getNearEntity(loc, distance);
	}

	@Override
	public Difficulty getDifficulty() {
		return getServerWorld().getDifficulty();
	}

	@Override
	public int getMaxHeight() {
		return getServerWorld().getMaxHeight();
	}

	@Override
	public int getMinHeight() {
		return getServerWorld().getMinHeight();
	}
	
	@Override
	public Object getDefault() {
		return getServerWorld().getDefault();
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return getServerWorld().isChunkLoaded(chunkX, chunkZ);
	}
	
	@Override
	public String toString() {
		return "CompensatedWorld{playered=" + (p == null ? null : p.getUniqueId()) + ",name=" + getName() + ",entities=" + getEntities().size() + "}";
	}
}
