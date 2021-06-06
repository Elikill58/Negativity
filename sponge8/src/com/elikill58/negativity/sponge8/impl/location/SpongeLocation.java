package com.elikill58.negativity.sponge8.impl.location;

import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge8.impl.block.SpongeBlock;

public class SpongeLocation extends Location {

	public SpongeLocation(ServerLocation location) {
		super(new SpongeWorld(location.world()), location.x(), location.y(), location.z());
	}
	
	public SpongeLocation(com.elikill58.negativity.api.location.World w, double x, double y, double z) {
		super(w, x, y, z);
	}
	
	public SpongeLocation(ServerWorld w, double x, double y, double z) {
		super(new SpongeWorld(w), x, y, z);
	}
	
	public SpongeLocation(com.elikill58.negativity.api.location.World w, Vector3d pos) {
		super(w, pos.x(), pos.y(), pos.z());
	}
	
	public SpongeLocation(ServerWorld w, Vector3d pos) {
		super(new SpongeWorld(w), pos.x(), pos.y(), pos.z());
	}

	@Override
	public Block getBlock() {
		return new SpongeBlock(getSpongeWorld().createSnapshot(getBlockX(), getBlockY(), getBlockZ()));
	}

	@Override
	public Object getDefault() {
		return ServerLocation.of(getSpongeWorld(), getX(), getY(), getZ());
	}
	
	private ServerWorld getSpongeWorld() {
		return (ServerWorld) getWorld().getDefault();
	}
}
