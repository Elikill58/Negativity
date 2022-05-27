package com.elikill58.negativity.sponge7.impl.location;

import org.spongepowered.api.world.World;

import com.elikill58.negativity.api.location.Location;

public class SpongeLocation {

	public static org.spongepowered.api.world.Location<World> fromCommon(Location loc){
		return new org.spongepowered.api.world.Location<World>((World) loc.getWorld().getDefault(), loc.getX(), loc.getY(), loc.getZ());
	}

	public static Location toCommon(org.spongepowered.api.world.Location<org.spongepowered.api.world.World> loc){
		return new Location(new SpongeWorld(loc.getExtent()), loc.getX(), loc.getY(), loc.getZ());
	}

	public static Location toCommon(World w, double x, double y, double z){
		return new Location(new SpongeWorld(w), x, y, z);
	}
}
