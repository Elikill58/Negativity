package com.elikill58.negativity.spigot.impl.location;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;

public class SpigotLocation extends Location {
	
	public SpigotLocation(org.bukkit.Location loc) {
		super(new SpigotWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public SpigotLocation(World w, double x, double y, double z) {
		super(w, x, y, z);
	}
	
	@Override
	public Block getBlock() {
		return new SpigotBlock(getBukkitWorld().getBlockAt(getBlockX(), getBlockY(), getBlockZ()));
	}
	
	@Override
	public Object getDefault() {
		return new org.bukkit.Location(getBukkitWorld(), getX(), getY(), getZ());
	}
	
	private org.bukkit.World getBukkitWorld() {
		return (org.bukkit.World) getWorld().getDefault();
	}
}
