package com.elikill58.negativity.sponge.impl.location;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge.impl.block.SpongeBlock;

public class SpongeLocation extends Location {

	public SpongeLocation(org.spongepowered.api.world.Location<World> location) {
		super(new SpongeWorld(location.getExtent()), location.getX(), location.getY(), location.getZ());
	}
	
	public SpongeLocation(com.elikill58.negativity.api.location.World w, double x, double y, double z) {
		super(w, x, y, z);
	}

	@Override
	public Block getBlock() {
		return new SpongeBlock(getSpongeExtent().createSnapshot(getBlockX(), getBlockY(), getBlockZ()));
	}

	@Override
	public Object getDefault() {
		return new org.spongepowered.api.world.Location<>(getSpongeExtent(), getX(), getY(), getZ());
	}
	
	private Extent getSpongeExtent() {
		return (Extent) getWorld().getDefault();
	}
}
