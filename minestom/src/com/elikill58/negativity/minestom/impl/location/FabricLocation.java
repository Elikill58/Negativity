package com.elikill58.negativity.minestom.impl.location;

import com.elikill58.negativity.api.location.Location;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FabricLocation {

	/*public static ServerWorld fromCommon(Location loc){
		return new ServerWorld((World) loc.getWorld().getDefault(), loc.getX(), loc.getY(), loc.getZ());
	}*/

	public static Location toCommon(World w, BlockPos loc){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> new FabricWorld(w)), loc.getX(), loc.getY(), loc.getZ());
	}

	public static Location toCommon(World w, Vec3d v){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> new FabricWorld(w)), v.x, v.y, v.z);
	}

	public static Location toCommon(World w, double x, double y, double z){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> new FabricWorld(w)), x, y, z);
	}
}
