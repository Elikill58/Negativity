package com.elikill58.negativity.minestom.impl.location;

import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.minestom.impl.block.MinestomBlock;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.universal.Version;

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
	public Block getBlockAt0(Location loc) {
		return getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	@Override
	public List<Entity> getEntities() {
		return w.getEntities().stream().map(MinestomEntityManager::getEntity).collect(Collectors.toList());
	}

	@Override
	public Difficulty getDifficulty() {
		return Difficulty.valueOf(MinecraftServer.getDifficulty().name());
	}

	@Override
	public int getMaxHeight() {
		return 255;
	}

	@Override
	public int getMinHeight() {
		return Version.getVersion().isNewerOrEquals(Version.V1_18) ? -64 : 0;
	}
	
	@Override
	public boolean isPVP() {
		return true;
	}

	@Override
	public Object getDefault() {
		return w;
	}

}
