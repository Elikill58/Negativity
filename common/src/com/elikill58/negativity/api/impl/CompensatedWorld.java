package com.elikill58.negativity.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockTransition;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.impl.block.CompensatedBlock;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.Adapter;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

public class CompensatedWorld extends World {

	protected final Player p;
	protected World serverWorld;
	protected ObjectList<Entity> entities = new ObjectArrayList<>();
	protected List<BlockTransition> transitions = new ArrayList<>();
	
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
		synchronized (transitions) {
			transitions.removeIf(BlockTransition::expired);
			for(BlockTransition t : transitions) {
				if(t.concern(x, y, z)) {
					return new CompensatedBlock(new Location(this, x, y, z), t.getOld());
				}
			}
		}
		return getServerWorld().getBlockAt(x, y, z);
	}

	public void addTimingBlock(long expireTime, Material next, int x, int y, int z) {
		long time = System.currentTimeMillis();
		transitions.add(new BlockTransition(time, time + expireTime, getServerWorld().getBlockAt0(x, y, z).getType(), next, x, y, z));
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(int id) {
		synchronized (entities) {
			entities.removeIf(et -> et == null || et.isSameId(id));
		}
	}

	public List<Entity> getEntities() {
		synchronized (entities) {
			entities.removeIf(Objects::isNull);
			return entities;
		}
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		Optional<Entity> opt = getEntities().stream().filter(e -> e.isSameId(id)).findFirst();
		return opt.isPresent() ? opt : getServerWorld().getEntityById(id);
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
	public String toString() {
		return "CompensatedWorld{playered=" + (p == null ? null : p.getUniqueId()) + ",name=" + getName() + ",entities=" + getEntities().size() + "}";
	}
}
