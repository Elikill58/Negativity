package com.elikill58.negativity.sponge.impl.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.world.server.ServerWorld;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge.impl.block.SpongeBlock;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.utils.Utils;

public class SpongeWorld extends World {

	private final ServerWorld w;
	
	public SpongeWorld(ServerWorld w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.key().asString();
	}

	@Override
	public Block getBlockAt(int x, int y, int z) {
		return new SpongeBlock(w.createSnapshot(x, y, z));
	}

	@Override
	public Block getBlockAt(Location loc) {
		return getBlockAt(loc.getBlockZ(), loc.getBlockY(), loc.getBlockZ());
	}

	@Override
	public List<Entity> getEntities() {
		List<Entity> list = new ArrayList<>();
		w.entities().forEach((e) -> list.add(SpongeEntityManager.getEntity(e)));
		return list;
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(Utils.getKey(w.difficulty()).value().toUpperCase(Locale.ROOT));
	}
	
	@Override
	public int getMaxHeight() {
		return w.maximumHeight();
	}
	
	@Override
	public int getMinHeight() {
		int min = w.min().y();
		return min > 0 ? 0 : min;
	}
	
	@Override
	public boolean isPVP() {
		return w.properties().pvp();
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
