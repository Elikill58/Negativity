package com.elikill58.negativity.api.location;

import java.util.Objects;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.universal.utils.Maths;

public class Location implements Cloneable {

	private World w;
	private double x, y, z;
	private float yaw = 0, pitch = 0;

	public Location(World w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location(World w, double x, double y, double z, float yaw, float pitch) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public World getWorld() {
		return w;
	}

	public void setWorld(World w) {
		this.w = w;
	}

	public double getX() {
		return x;
	}

	public int getBlockX() {
		return (int) x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public int getBlockY() {
		return (int) y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockZ() {
		return (int) z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public Location sub(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public Location sub(Location other) {
		this.x -= other.getX();
		this.y -= other.getY();
		this.z -= other.getZ();
		return this;
	}

	public Location sub(Vector other) {
		this.x -= other.getX();
		this.y -= other.getY();
		this.z -= other.getZ();
		return this;
	}

	public Location add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public Location add(Location other) {
		this.x += other.getX();
		this.y += other.getY();
		this.z += other.getZ();
		return this;
	}

	public Location add(Vector other) {
		this.x += other.getX();
		this.y += other.getY();
		this.z += other.getZ();
		return this;
	}

	public Vector toVector() {
		return new Vector(this);
	}
	
	public Block getBlock() {
		return w.getBlockAt(getBlockX(), getBlockY(), getBlockZ());
	}

	public Vector getDirection() {
		double rotX = getYaw();
		double rotY = getPitch();

		double y = -Math.sin(Math.toRadians(rotY));

		double xz = Math.cos(Math.toRadians(rotY));

		double x = -xz * Math.sin(Math.toRadians(rotX));
		double z = xz * Math.cos(Math.toRadians(rotX));

		return new Vector(x, y, z);
	}
	
	public double distance(Location o) {
		return Math.sqrt(distanceSquared(o));
	}

	public double distanceSquared(Location o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}
		if ((o.getWorld() == null) || (getWorld() == null)) {
			throw new IllegalArgumentException("Cannot measure distance to a null world");
		}
		if (!o.getWorld().getName().equals(getWorld().getName())) {
			throw new IllegalArgumentException(
					"Cannot measure distance between " + getWorld().getName() + " and " + o.getWorld().getName());
		}
		return Maths.square(this.x - o.x) + Maths.square(this.y - o.y) + Maths.square(this.z - o.z);
	}

	@Override
	public boolean equals(Object obj) {
		Objects.requireNonNull(obj);
		if(!(obj instanceof Location))
			return false;
		Location loc = (Location) obj;
		if(!w.getName().equals(loc.getWorld().getName()))
			return false;
		return x == loc.x && y == loc.y && z == loc.z;
	}
	
	@Override
	public String toString() {
		return "Location{w=" + w + ",x=" + x + ",y=" + y + ",z=" + z + ",yaw=" + yaw + ",pitch=" + pitch + '}';
	}
	
	@Override
	public Location clone() {
		try {
			return (Location) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
