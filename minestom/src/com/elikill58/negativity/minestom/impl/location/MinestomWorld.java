package com.elikill58.negativity.minestom.impl.location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.minestom.impl.block.MinestomBlock;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntity;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class MinestomWorld extends World {

	private final Instance w;

	public MinestomWorld(Instance w) {
		this.w = w;
	}

	@Override
	public String getName() {
		return w.getUniqueId().toString();
	}

	@Override
	public Block getBlockAt0(int x, int y, int z) {
		Pos pos = new Pos(x, y, z);
		return new MinestomBlock(w.getBlock(pos), w, pos);
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(MinecraftServer.getDifficulty().name());
	}

	@Override
	public int getMaxHeight() {
		return w.getDimensionType().getMaxY();
	}

	@Override
	public int getMinHeight() {
		return w.getDimensionType().getMinY();
	}
	
	@Override
	public List<Entity> getEntities() {
		return w.getEntities().stream().map(MinestomEntity::new).collect(Collectors.toList());
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		return w.getEntities().stream().filter(e -> e.getEntityId() == id).findFirst().map(MinestomEntity::new);
	}
	
	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		return w.getNearbyEntities(MinestomLocation.fromCommon(loc), distance).stream().map(MinestomEntity::new).collect(Collectors.toList());
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return w.isChunkLoaded(chunkX, chunkZ);
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
