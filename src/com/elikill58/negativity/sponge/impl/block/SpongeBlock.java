package com.elikill58.negativity.sponge.impl.block;

import org.spongepowered.api.block.BlockSnapshot;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge.impl.location.SpongeLocation;

public class SpongeBlock extends Block {

	private final BlockSnapshot block;
	
	public SpongeBlock(BlockSnapshot block) {
		this.block = block;
	}

	@Override
	public Material getType() {
		return ItemRegistrar.getInstance().get(block.getState().getId(), block.getState().getName());
	}

	@Override
	public int getX() {
		return block.getPosition().getX();
	}

	@Override
	public int getY() {
		return block.getPosition().getY();
	}

	@Override
	public int getZ() {
		return block.getPosition().getZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		// TODO implement getRelative
		return null;//new SpongeBlock(block.getRelative(org.bukkit.block.BlockFace.valueOf(blockFace.name())));
	}

	@Override
	public Location getLocation() {
		return new SpongeLocation(block.getLocation().orElse(null));
	}

	@Override
	public boolean isLiquid() {
		// TODO implement isLiquid
		return false;
	}

	@Override
	public void setType(Material type) {
		// TODO implement setType
	}

	@Override
	public Object getDefault() {
		return block;
	}

}
