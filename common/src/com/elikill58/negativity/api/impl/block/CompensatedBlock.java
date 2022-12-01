package com.elikill58.negativity.api.impl.block;

import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;

public class CompensatedBlock extends Block {

	private final Location loc;
	private Material type;
	
	public CompensatedBlock(Location loc, Material type) {
		this.loc = loc;
		this.type = type;
	}
	
	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public Material getType() {
		return type;
	}

	@Override
	public int getX() {
		return loc.getBlockX();
	}

	@Override
	public int getY() {
		return loc.getBlockY();
	}

	@Override
	public int getZ() {
		return loc.getBlockZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		if(blockFace.equals(BlockFace.SELF))
			return this;
		return loc.getWorld().getBlockAt(loc.clone().add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ()));
	}

	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}

	@Override
	public void setType(Material type) {
		this.type = type;
	}

	@Override
	public boolean isWaterLogged() {
		return false;
	}

	@Override
	public Object getDefault() {
		return this;
	}
}
