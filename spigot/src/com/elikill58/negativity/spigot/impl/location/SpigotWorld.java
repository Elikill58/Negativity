package com.elikill58.negativity.spigot.impl.location;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntity;
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
		return new SpigotBlock(w.getBlockAt(x, y, z));
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
		return w.getEntities().stream().map(SpigotEntity::new).collect(Collectors.toList());
	}
	
	@Override
	public Optional<Entity> getEntityById(int id) {
		/*if(!Bukkit.isPrimaryThread()) // prevent error
			return Optional.empty();*/
		return w.getEntities().stream().filter(e -> e.getEntityId() != id).findFirst().map(SpigotEntity::new);
	}

	@Override
	public Object getDefault() {
		return w;
	}
}