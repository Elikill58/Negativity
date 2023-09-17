package com.elikill58.negativity.spigot.impl.location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntity;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

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
	public Block getBlockAt0(int x, int y, int z) {
		if(!w.isChunkLoaded(x / 16, z / 16))
			return emptyBlock;
		try {
			return new SpigotBlock(w.getBlockAt(x, y, z));
		} catch (IllegalStateException e) {
			// tried to load async
			Adapter.getAdapter().getScheduler().run(() -> w.getBlockAt(x, y, z)); // ask for block, to be sure the chunk is loaded but sync
			return emptyBlock;
		}
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
	public List<Entity> getEntities() {
		if(!Bukkit.isPrimaryThread()) // prevent error
			return SpigotVersionAdapter.getVersionAdapter().getEntities(w).stream().map(SpigotEntity::new).collect(Collectors.toList());
		return w.getEntities().stream().map(SpigotEntity::new).collect(Collectors.toList());
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		return getEntities().stream().filter(e -> e.getEntityId() == id).findFirst();
	}
	
	@Override
	public List<Entity> getNearEntity(Location loc, double distance) {
		if(!Bukkit.isPrimaryThread()) // prevent error
			return getEntities().stream().filter(e -> e.getLocation().distance(loc) <= distance).collect(Collectors.toList()); // not optimized but working
		return w.getNearbyEntities(SpigotLocation.fromCommon(loc), distance, distance, distance).stream().map(SpigotEntity::new).collect(Collectors.toList());
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