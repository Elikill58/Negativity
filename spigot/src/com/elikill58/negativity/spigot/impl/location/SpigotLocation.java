package com.elikill58.negativity.spigot.impl.location;

import org.bukkit.World;

import com.elikill58.negativity.api.location.Location;

public class SpigotLocation {

	public static org.bukkit.Location fromCommon(Location loc){
		return new org.bukkit.Location((World) loc.getWorld().getDefault(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	public static Location toCommon(org.bukkit.Location loc){
		return new Location(new SpigotWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}
}
