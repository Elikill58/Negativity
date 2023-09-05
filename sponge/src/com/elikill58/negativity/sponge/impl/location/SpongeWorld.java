package com.elikill58.negativity.sponge.impl.location;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge.impl.block.SpongeBlock;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntity;
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
	public Block getBlockAt0(int x, int y, int z) {
		return new SpongeBlock(w.createSnapshot(x, y, z));
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
	public List<Entity> getEntities() {
		return w.entities().stream().map(SpongeEntity::new).collect(Collectors.toList());
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		return w.entities().stream().filter(et -> et.uniqueId().hashCode() == id).findFirst().map(SpongeEntity::new);
	}
	
	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		return w.nearbyEntities(new Vector3d(loc.getX(), loc.getY(), loc.getZ()), distance).stream().map(SpongeEntity::new).collect(Collectors.toList());
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return w.isChunkLoaded(chunkX, 0, chunkZ, false);
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
