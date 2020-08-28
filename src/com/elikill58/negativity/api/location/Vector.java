package com.elikill58.negativity.api.location;

import static com.elikill58.negativity.universal.utils.Maths.square;
import static com.elikill58.negativity.universal.utils.Maths.floor;

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

	public Vector() {
		
	}
	
	public Vector(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
	}
	
	public Vector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector add(Vector vec) {
		this.x += vec.x;
		this.y += vec.y;
		this.z += vec.z;
		return this;
	}

	public Vector subtract(Vector vec) {
		this.x -= vec.x;
		this.y -= vec.y;
		this.z -= vec.z;
		return this;
	}

	public Vector multiply(Vector vec) {
		this.x *= vec.x;
		this.y *= vec.y;
		this.z *= vec.z;
		return this;
	}

	public Vector divide(Vector vec) {
		this.x /= vec.x;
		this.y /= vec.y;
		this.z /= vec.z;
		return this;
	}

	public Vector copy(Vector vec) {
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}

	public double length() {
		return Math.sqrt(
				square(this.x) + square(this.y) + square(this.z));
	}

	public double lengthSquared() {
		return square(this.x) + square(this.y) + square(this.z);
	}

	public double distance(Vector o) {
		return Math.sqrt(square(this.x - o.x) + square(this.y - o.y)
				+ square(this.z - o.z));
	}

	public double distanceSquared(Vector o) {
		return square(this.x - o.x) + square(this.y - o.y)
				+ square(this.z - o.z);
	}

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
		double length = length();

		this.x /= length;
		this.y /= length;
		this.z /= length;

		return this;
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
		return square(origin.x - this.x) + square(origin.y - this.y)
				+ square(origin.z - this.z) <= square(radius);
	}

	public double getX() {
		return this.x;
	}

	public int getBlockX() {
		return floor(this.x);
	}

	public double getY() {
		return this.y;
	}

	public int getBlockY() {
		return floor(this.y);
	}

	public double getZ() {
		return this.z;
	}

	public int getBlockZ() {
		return floor(this.z);
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

	public String toString() {
		return this.x + "," + this.y + "," + this.z;
	}
}
