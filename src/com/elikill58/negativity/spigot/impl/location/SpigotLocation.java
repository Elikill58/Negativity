package com.elikill58.negativity.spigot.impl.location;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.block.SpigotBlock;

public class SpigotLocation extends Location {

	private final org.bukkit.Location loc;
	
	public SpigotLocation(org.bukkit.Location loc) {
		super(new SpigotWorld(loc.getWorld()), loc.getX(), loc.getY(), loc.getZ());
		this.loc = loc;
	}
	
	public SpigotLocation(World w, double x, double y, double z) {
		super(w, x, y, z);
		this.loc = new org.bukkit.Location((org.bukkit.World) w.getDefault(), x, y, z);
	}

	@Override
	public Block getBlock() {
		return new SpigotBlock(new org.bukkit.Location(loc.getWorld(), getX(), getY(), getZ()).getBlock());
	}

	@Override
	public Object getDefault() {
		org.bukkit.Location loc = this.loc.clone();
		loc.setX(getX());
		loc.setY(getY());
		loc.setZ(getZ());
		loc.setYaw(getYaw());
		loc.setPitch(getPitch());
		return loc;
	}

}
