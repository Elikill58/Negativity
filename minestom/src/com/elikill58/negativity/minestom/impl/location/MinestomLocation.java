package com.elikill58.negativity.minestom.impl.location;

import com.elikill58.negativity.api.location.Location;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class MinestomLocation {

	public static Location toCommon(Instance w, Pos loc){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.getUniqueId().toString(), a -> new MinestomWorld(w)), loc.x(), loc.y(), loc.z());
	}

	public static Location toCommon(Instance w, Point loc){
		return new Location(com.elikill58.negativity.api.location.World.getWorld(w.getUniqueId().toString(), a -> new MinestomWorld(w)), loc.x(), loc.y(), loc.z());
	}

	public static Pos fromCommon(Location loc){
		return new Pos(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
}
