package com.elikill58.negativity.spigot.impl.location;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.common.block.Block;
import com.elikill58.negativity.common.entity.Entity;
import com.elikill58.negativity.common.location.Difficulty;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntity;

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
		return new SpigotBlock(w.getBlockAt(loc.getBlockZ(), loc.getBlockY(), loc.getBlockZ()));
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.getEntities().forEach((e) -> list.add(new SpigotEntity(e)));
		return list;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().name());
	}

	@Override
	public Object getDefaultWorld() {
		return w;
	}
}
