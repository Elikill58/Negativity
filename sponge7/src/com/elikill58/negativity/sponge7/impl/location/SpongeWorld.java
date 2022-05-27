package com.elikill58.negativity.sponge7.impl.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge7.impl.block.SpongeBlock;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;

public class SpongeWorld extends World {

	private final org.spongepowered.api.world.World w;
	
	public SpongeWorld(org.spongepowered.api.world.World w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getName();
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return new SpongeBlock(w.createSnapshot(x, y, z));
	}

	@Override
	public Block getBlockAt(Location loc) {
		return getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.getEntities().forEach((e) -> list.add(SpongeEntityManager.getEntity(e)));
		return list;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(w.getDifficulty().getId().toUpperCase(Locale.ROOT));
	}
	
	@Override
	public int getMaxHeight() {
		return w.getDimension().getBuildHeight();
	}
	
	@Override
	public int getMinHeight() {
		return 0;
	}
	
	@Override
	public boolean isPVP() {
		return w.getProperties().isPVPEnabled();
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
