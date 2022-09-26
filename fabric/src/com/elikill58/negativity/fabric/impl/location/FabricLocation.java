package com.elikill58.negativity.fabric.impl.location;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.fabric.GlobalFabricNegativity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FabricLocation {

	public static Location toCommon(World w, BlockPos loc){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> GlobalFabricNegativity.createWorld(w)), loc.getX(), loc.getY(), loc.getZ());
	}

	public static Location toCommon(World w, Vec3d v){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> GlobalFabricNegativity.createWorld(w)), v.x, v.y, v.z);
	}

	public static Location toCommon(World w, double x, double y, double z){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.asString(), a -> GlobalFabricNegativity.createWorld(w)), x, y, z);
	}
}
