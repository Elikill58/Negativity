package com.elikill58.negativity.spigot.impl.block;

import com.elikill58.negativity.common.block.Block;
import com.elikill58.negativity.common.block.BlockFace;
import com.elikill58.negativity.common.item.ItemRegistrar;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;

public class SpigotBlock extends Block {

	private final org.bukkit.block.Block block;

	public SpigotBlock(org.bukkit.block.Block block) {
		this.block = block;
	}

	@Override
	public Material getType() {
		return ItemRegistrar.getInstance().get(block.getType().name());
	}

	@Override
	public int getX() {
		return block.getX();
	}

	@Override
	public int getY() {
		return block.getY();
	}

	@Override
	public int getZ() {
		return block.getZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		return new SpigotBlock(block.getRelative(org.bukkit.block.BlockFace.valueOf(blockFace.name())));
	}

	@Override
	public Location getLocation() {
		return new SpigotLocation(block.getLocation());
	}

	@Override
	public boolean isLiquid() {
		return block.isLiquid();
	}

	@Override
	public void setType(Material type) {
		block.setType((org.bukkit.Material) type.getDefaultMaterial());
	}

}
