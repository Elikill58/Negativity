package com.elikill58.negativity.api.impl.server.block;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.impl.server.item.CompensatedMaterial;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;

public class EmptyBlock extends Block {

	private final World w;
	
	public EmptyBlock(World w) {
		this.w = w;
	}
	
	@Override
	public Object getDefault() {
		return this;
	}

	@Override
	public Location getLocation() {
		return new Location(w, 0, 0, 0);
	}

	@Override
	public Material getType() {
		return new CompensatedMaterial("air"); // prevent error
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getZ() {
		return 0;
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		return new EmptyBlock(w);
	}

	@Override
	public boolean isLiquid() {
		return false;
	}

	@Override
	public void setType(Material type) {
		throw new RuntimeException("Can't set type of empty block.");
	}

	@Override
	public boolean isWaterLogged() {
		return false;
	}

}
