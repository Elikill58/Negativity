package com.elikill58.negativity.sponge7.impl.location;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge7.impl.block.SpongeBlock;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntity;
import com.flowpowered.math.vector.Vector3d;

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
	public Block getBlockAt0(int x, int y, int z) {
		return new SpongeBlock(w.createSnapshot(x, y, z));
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
	public List<Entity> getEntities() {
		return w.getEntities().stream().map(SpongeEntity::new).collect(Collectors.toList());
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		return w.getEntities(et -> et.getUniqueId().hashCode() == id).stream().findFirst().map(SpongeEntity::new);
	}
	
	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		return w.getNearbyEntities(new Vector3d(loc.getX(), loc.getY(), loc.getZ()), distance).stream().map(SpongeEntity::new).collect(Collectors.toList());
	}

	@Override
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return w.getChunk(chunkX, 0, chunkZ).isPresent();
	}

	@Override
	public Object getDefault() {
		return w;
	}
}
