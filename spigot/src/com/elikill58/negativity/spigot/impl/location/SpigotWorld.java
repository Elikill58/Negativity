package com.elikill58.negativity.spigot.impl.location;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Difficulty;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;
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
		return new SpigotBlock(w.getBlockAt(x, y, z));
	}

	@Override
	public Block getBlockAt0(Location loc) {
		return new SpigotBlock(w.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
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
	public Object getDefault() {
		return w;
	}
}