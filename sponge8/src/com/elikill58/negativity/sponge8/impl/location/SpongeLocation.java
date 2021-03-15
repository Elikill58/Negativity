package com.elikill58.negativity.sponge8.impl.location;

import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge8.impl.block.SpongeBlock;

public class SpongeLocation extends Location {

	private final ServerLocation loc;
	
	public SpongeLocation(ServerLocation location) {
		super(new SpongeWorld(location.getWorld()), location.getX(), location.getY(), location.getZ());
		this.loc = location;
	}
	
	public SpongeLocation(com.elikill58.negativity.api.location.World w, double x, double y, double z) {
		super(w, x, y, z);
		this.loc = ServerLocation.of((ServerWorld) w.getDefault(), x, y, z);
	}
	
	public SpongeLocation(ServerWorld w, double x, double y, double z) {
		super(new SpongeWorld(w), x, y, z);
		this.loc = ServerLocation.of(w, x, y, z);
	}
	
	public SpongeLocation(com.elikill58.negativity.api.location.World w, Vector3d pos) {
		super(w, pos.getX(), pos.getY(), pos.getZ());
		this.loc = ServerLocation.of((ServerWorld) w.getDefault(), pos);
	}
	
	public SpongeLocation(ServerWorld w, Vector3d pos) {
		super(new SpongeWorld(w), pos.getX(), pos.getY(), pos.getZ());
		this.loc = ServerLocation.of(w, pos);
	}

	@Override
	public Block getBlock() {
		return new SpongeBlock(loc.createSnapshot());
	}

	@Override
	public Object getDefault() {
		return loc;
	}
}
