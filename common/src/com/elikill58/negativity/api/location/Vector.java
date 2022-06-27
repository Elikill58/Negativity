package com.elikill58.negativity.api.location;

import static com.elikill58.negativity.universal.utils.Maths.roundLoc;
import static com.elikill58.negativity.universal.utils.Maths.square;

import com.elikill58.negativity.api.maths.Point;

public class Vector implements Cloneable {

	public static final Vector ZERO = new Vector(0, 0, 0);
	public static final Vector UNIT_X = new Vector(1, 0, 0);
	public static final Vector UNIT_Y = new Vector(0, 1, 0);
	public static final Vector UNIT_Z = new Vector(0, 0, 1);
	public static final Vector ONE = new Vector(1, 1, 1);
	public static final Vector RIGHT = UNIT_X;
	public static final Vector UP = UNIT_Y;

	protected double x;
	protected double y;
	protected double z;

	/**
	 * Create vector according to the given location
	 * 
	 * @param loc the location that must to be vectorised
	 */
	public Vector(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}
	
	/**
	 * Create vector with rounded values
	 * 
	 * @param x the X vector movement
	 * @param y the Y vector movement
	 * @param z the Z vector movement
	 */
	public Vector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create vector with double values
	 * 
	 * @param x the X vector movement
	 * @param y the Y vector movement
	 * @param z the Z vector movement
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Create vector with float values
	 * 
	 * @param x the X vector movement
	 * @param y the Y vector movement
	 * @param z the Z vector movement
	 */
	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Convert this vector into a new one with int block
	 * 
	 * @return a new vector
	 */
	public Vector toBlockVector() {
		return new Vector(getBlockX(), getBlockY(), getBlockZ());
	}

	/**
	 * Add vector to another vector
	 * 
	 * @param vec the added vector movement
	 * @return this vector with new movement
	 */
	public Vector add(Vector vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}

	/**
	 * Remove vector to another vector
	 * 
	 * @param vec the remoevd vector movement
	 * @return this vector
	 */
	public Vector subtract(Vector vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	/**
	 * Multiply the current vector to the given one
	 * 
	 * @param vec the multiplying vector
	 * @return this vector
	 */
	public Vector multiply(Vector vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		return this;
	}

	/**
	 * Divide the current vector to the given one
	 * 
	 * @param vec the divided vector
	 * @return this vector
	 */
	public Vector divide(Vector vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;
		return this;
	}

	/**
	 * Divide the current vector to the given length
	 * 
	 * @param len the length
	 * @return this vector
	 */
	public Vector divide(double len) {
		this.x /= len;
		this.y /= len;
		this.z /= len;
		return this;
	}

	/**
	 * Copy the given vector to the current.
	 * 
	 * @param vec the new vector values of this one
	 * @return the vector with given value
	 */
	public Vector copy(Vector vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	public double length() {
		return Math.sqrt(square(this.x) + square(this.y) + square(this.z));
	}

	public double lengthSquared() {
		return square(this.x) + square(this.y) + square(this.z);
	}

	/**
	 * Get the distance between this vector nd the given one
	 * 
	 * @param o the vector which will compare to
	 * @return the distance
	 */
	public double distance(Vector o) {
		return Math.sqrt(square(this.x - o.x) + square(this.y - o.y) + square(this.z - o.z));
	}

	/**
	 * Get the distance squared between this vector nd the given one
	 * 
	 * @param o the vector which will compare to
	 * @return the distance squared
	 */
	public double distanceSquared(Vector o) {
		return square(this.x - o.x) + square(this.y - o.y) + square(this.z - o.z);
	}

	/**
	 * Get the angle between 2 vectors.
	 * 
	 * @param other the vector to compare with
	 * @return the angle
	 */
	public float angle(Vector other) {
		double dot = dot(other) / (length() * other.length());

		return (float) Math.acos(dot);
	}

	public Vector multiply(int m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public Vector multiply(double m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public Vector multiply(float m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}

	public double dot(Vector other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public double dot(Point other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
	}

	public Vector crossProduct(Vector o) {
		double newX = this.y * o.z - o.y * this.z;
		double newY = this.z * o.x - o.z * this.x;
		double newZ = this.x * o.y - o.x * this.y;

		this.x = newX;
		this.y = newY;
		this.z = newZ;
		return this;
	}

	public Vector normalize() {
		return divide(length());
	}

	public Vector zero() {
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
		return this;
	}

	public boolean isInAABB(Vector min, Vector max) {
		return (this.x >= min.x) && (this.x <= max.x) && (this.y >= min.y) && (this.y <= max.y) && (this.z >= min.z)
				&& (this.z <= max.z);
	}

	public boolean isInSphere(Vector origin, double radius) {
		return square(origin.x - this.x) + square(origin.y - this.y) + square(origin.z - this.z) <= square(radius);
	}

	public double getX() {
		return this.x;
	}

	public int getBlockX() {
		return roundLoc(this.x);
	}

	public double getY() {
		return this.y;
	}

	public int getBlockY() {
		return roundLoc(this.y);
	}

	public double getZ() {
		return this.z;
	}

	public int getBlockZ() {
		return roundLoc(this.z);
	}

	public Vector setX(int x) {
		this.x = x;
		return this;
	}

	public Vector setX(double x) {
		this.x = x;
		return this;
	}

	public Vector setX(float x) {
		this.x = x;
		return this;
	}

	public Vector setY(int y) {
		this.y = y;
		return this;
	}

	public Vector setY(double y) {
		this.y = y;
		return this;
	}

	public Vector setY(float y) {
		this.y = y;
		return this;
	}

	public Vector setZ(int z) {
		this.z = z;
		return this;
	}

	public Vector setZ(double z) {
		this.z = z;
		return this;
	}

	public Vector setZ(float z) {
		this.z = z;
		return this;
	}

	public Vector negate() {
		return new Vector(-this.x, -this.y, -this.z);
	}
	
	public Location toLocation(World w) {
		return new Location(w, x, y, z);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Vector)) {
			return false;
		}
		Vector other = (Vector) obj;

		return (Math.abs(this.x - other.x) < 1.0E-6D) && (Math.abs(this.y - other.y) < 1.0E-6D)
				&& (Math.abs(this.z - other.z) < 1.0E-6D) && (getClass().equals(obj.getClass()));
	}

	@Override
	public Vector clone() {
		try {
			return (Vector) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
	
	@Override
	public int hashCode() {
		int hash = getBlockX();
	    hash = 31 * hash + getBlockY();
	    hash = 31 * hash + getBlockZ();
	    return hash;
	}

	@Override
	public String toString() {
		return "Vector{x=" + parseDouble(this.x, 10) + ",y=" + parseDouble(this.y, 10) + ",z=" + parseDouble(this.z, 10) + ",len="+ parseDouble(length(), 10) + "}";
	}

	private String parseDouble(double d, int precision) {
		return d == 0.0 ? "0,0" : String.format("%." + precision + "f", d);
	}
	
	public String toShowableString() {
		return "x: " + String.format("%.2f", this.x) + ", y: " + String.format("%.2f", this.y) + ", z: " + String.format("%.2f", this.z);
	}
}
