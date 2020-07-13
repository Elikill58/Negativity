package com.elikill58.negativity.common.location;

import com.elikill58.negativity.common.block.Block;

public abstract class Location implements Cloneable {

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

	public abstract Vector toVector();

	public abstract Block getBlock();

	public Vector getDirection() {
		Vector vector = new Vector();

		double rotX = getYaw();
		double rotY = getPitch();

		vector.setY(-Math.sin(Math.toRadians(rotY)));

		double xz = Math.cos(Math.toRadians(rotY));

		vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
		vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

		return vector;
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
