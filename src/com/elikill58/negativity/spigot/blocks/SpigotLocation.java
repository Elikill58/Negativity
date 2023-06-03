package com.elikill58.negativity.spigot.blocks;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.utils.LocationUtils;

public final class SpigotLocation extends Location {

	private SpigotWorld w;

	public SpigotLocation(Location loc) {
		super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.w = SpigotWorld.getWorld(loc.getWorld());
	}

	public SpigotLocation(SpigotWorld w, double x, double y, double z) {
		super(w.getWorld(), x, y, z);
		this.w = w;
	}

	public SpigotLocation(SpigotWorld w, double x, double y, double z, float yaw, float pitch) {
		super(w.getWorld(), x, y, z, yaw, pitch);
		this.w = w;
	}

	public SpigotWorld getSpigotWorld() {
		return w;
	}

	public void setSpigotWorld(SpigotWorld w) {
		this.w = w;
	}

	public Vector toBlockVector() {
		return new Vector(getBlockX(), getBlockY(), getBlockZ());
	}
	
	public double distanceXZ(Location loc) {
		return Math.sqrt(LocationUtils.square(getX() - loc.getX()) + LocationUtils.square(getZ() - loc.getZ()));
	}
	
	@Override
	public SpigotLocation add(double x, double y, double z) {
		super.add(x, y, z);
		return this;
	}
	
	@Override
	public SpigotLocation add(Location loc) {
		super.add(loc);
		return this;
	}
	
	@Override
	public SpigotLocation subtract(double x, double y, double z) {
		super.subtract(x, y, z);
		return this;
	}
	
	@Override
	public SpigotLocation subtract(Location loc) {
		super.subtract(loc);
		return this;
	}
	
	@Override
	public Block getBlock() {
		return w.getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}
	
	@Override
	public SpigotLocation clone() {
		return (SpigotLocation) super.clone();
	}
}
