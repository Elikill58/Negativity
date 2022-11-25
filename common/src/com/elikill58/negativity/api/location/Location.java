package com.elikill58.negativity.api.location;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockChecker;
import com.elikill58.negativity.universal.utils.Maths;

public final class Location implements Cloneable {

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
		return Maths.roundLoc(x);
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public int getBlockY() {
		return Maths.roundLoc(y);
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockZ() {
		return Maths.roundLoc(z);
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

	public Vector toBlockVector() {
		return new Vector(getBlockX(), getBlockY(), getBlockZ());
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
	
	/**
	 * Get distance with given location
	 * 
	 * @param o other location
	 * @return distance to given location
	 */
	public double distance(@NonNull Location o) {
		return Math.sqrt(distanceSquared(o));
	}
	
	/**
	 * Get squared distance with given location
	 * 
	 * @param o other location
	 * @return distance to given location
	 */
	public double distanceSquared(@NonNull Location o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}
		if ((o.getWorld() == null) || (getWorld() == null)) {
			return 0;
		}
		return Maths.square(this.x - o.x) + Maths.square(this.y - o.y) + Maths.square(this.z - o.z);
	}
	
	/**
	 * Get distance in X/Z axis with given location
	 * 
	 * @param o other location
	 * @return distance to given location
	 */
	public double distanceXZ(@NonNull Location o) {
		return Math.sqrt(distanceSquaredXZ(o));
	}
	
	/**
	 * Get squared distance in X/Z axis with given location
	 * 
	 * @param o other location
	 * @return distance to given location
	 */
	public double distanceSquaredXZ(@NonNull Location o) {
		if (o == null) {
			throw new IllegalArgumentException("Cannot measure distance to a null location");
		}
		if ((o.getWorld() == null) || (getWorld() == null)) {
			return 0;
		}
		if (!o.getWorld().getName().equals(getWorld().getName())) {
			return 0;
		}
		return Maths.square(this.x - o.x) + Maths.square(this.z - o.z);
	}

	/**
	 * Get block checker with current size
	 * 
	 * @param size the size of checker (used for x/y/z)
	 * @return the checker
	 */
	public BlockChecker getBlockChecker(double size) {
        return getBlockChecker(size, size, size);
	}

	/**
	 * Get block checker with current size
	 * 
	 * @param size the size of checker (used for x/z)
	 * @return the checker
	 */
	public BlockChecker getBlockCheckerXZ(double size) {
        return getBlockChecker(size, 0, size);
	}

	/**
	 * Get block checker with current size
	 * 
	 * @param sizeX the X size
	 * @param sizeY the Y size
	 * @param sizeZ the Z size
	 * @return the checker
	 */
	public BlockChecker getBlockChecker(double sizeX, double sizeY, double sizeZ) {
        List<Block> blocks = new ArrayList<>();

        double minX = x - sizeX, maxX = x + sizeX;
        double minY = y - sizeY, maxY = y + sizeY;
        double minZ = z - sizeZ, maxZ = z + sizeZ;
        
        for (double x = minX; x <= maxX; x += (maxX - minX)) {
        	if(sizeY == 0) {
                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
                    blocks.add(w.getBlockAt(x, y, z));
                }
        	} else {
	            for (double y = minY; y <= maxY + 0.01; y += (maxY - minY)) {
	                for (double z = minZ; z <= maxZ; z += (maxZ - minZ)) {
	                    blocks.add(w.getBlockAt(x, y, z));
	                }
	            }
        	}
        }
        return new BlockChecker(blocks);
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
	public int hashCode() {
		int hash = getBlockX();
	    hash = 31 * hash + getBlockY();
	    hash = 31 * hash + getBlockZ();
	    return hash * w.getName().hashCode();
	}
	
	@Override
	public String toString() {
		return "Location{w=" + (w == null ? null : w.getName()) + ",x=" + x + ",y=" + y + ",z=" + z + ",yaw=" + yaw + ",pitch=" + pitch + '}';
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
