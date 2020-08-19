package com.elikill58.negativity.sponge.impl.block;

import org.spongepowered.api.block.BlockState;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;

public class SpongeBlock extends Block {

	private final BlockState block;

	// TODO FIX SPONGE BLOCKs
	public SpongeBlock(BlockState block) {
		this.block = block;
	}

	@Override
	public Material getType() {
		return ItemRegistrar.getInstance().get(block.getType().getId());
	}

	@Override
	public int getX() {
		return 0;//block.getX();
	}

	@Override
	public int getY() {
		return 0;//block.getY();
	}

	@Override
	public int getZ() {
		return 0;//block.getZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		return null;//new SpongeBlock(block.getRelative(org.bukkit.block.BlockFace.valueOf(blockFace.name())));
	}

	@Override
	public Location getLocation() {
		return null;//new SpongeLocation(block.getLocation());
	}

	@Override
	public boolean isLiquid() {
		return false;//block.isLiquid();
	}

	@Override
	public void setType(Material type) {
		//block.setType(type.getDefault());
	}

	@Override
	public Object getDefault() {
		return block;
	}

}
