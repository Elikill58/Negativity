package com.elikill58.negativity.spigot.impl.location;

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

public class SpigotWorld extends World {

	private final org.bukkit.World w;
	
	public SpigotWorld(org.bukkit.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getName();
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return new SpigotBlock(w.getBlockAt(x, y, z));
	}

	@Override
	public Block getBlockAt(Location loc) {
		return new SpigotBlock(w.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public List<Entity> getEntities() {
		synchronized (w) {
			return SpigotVersionAdapter.getVersionAdapter().getEntities(w).stream().map(SpigotEntityManager::getEntity).collect(Collectors.toList());
		}
	}
	
	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().name());
	}

	@Override
	public Object getDefault() {
		return w;
	}
}
