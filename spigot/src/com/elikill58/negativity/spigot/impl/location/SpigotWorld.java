package com.elikill58.negativity.spigot.impl.location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Version;

public class SpigotWorld extends World {

	private final org.bukkit.World w;
	private List<Entity> entities;
	
	public SpigotWorld(org.bukkit.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getName();
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		return new SpigotBlock(w.getBlockAt(x, y, z));
	}

	@Override
	public Block getBlockAt0(Location loc) {
		return new SpigotBlock(w.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	public void add(Entity e) {
		if(entities == null)
			clearEntities();
		else {
			synchronized (entities) {
				entities.add(e);
			}
		}
	}

	public void remove(Entity e) {
		if(entities == null) {
			clearEntities();
			return;
		}
		synchronized (entities) {
			entities.remove(e);
		}
	}

	public void clearEntities() {
		this.entities = SpigotVersionAdapter.getVersionAdapter().getEntities(w).stream().map(SpigotEntityManager::getEntity).collect(Collectors.toList());
	}
	
	private void checkEntities() {
		if(entities == null || entities.isEmpty())
			clearEntities();
	}
	
	@Override
	public List<Entity> getEntities() {
		checkEntities();
		return new ArrayList<>(entities);// SpigotVersionAdapter.getVersionAdapter().getEntities(w).stream().map(SpigotEntityManager::getEntity).collect(Collectors.toList());
	}
	
	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().name());
	}
	
	@Override
	public int getMaxHeight() {
		return w.getMaxHeight();
	}
	
	@Override
	public int getMinHeight() {
		return Version.getVersion().isNewerOrEquals(Version.V1_18) ? -64 : 0;
	}

	@Override
	public boolean isPVP() {
		return w.getPVP();
	}

	@Override
	public Object getDefault() {
		return w;
	}
}
