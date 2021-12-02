package com.elikill58.negativity.api.maths;

import com.elikill58.negativity.api.location.Vector;

public class Point implements Cloneable {

	public double x, y, z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double distance(Point o) {
		return o == null ? 0 : Math.sqrt(distanceSquared(o));
	}

	public double distanceSquared(Point o) {
		return square(this.x - o.x) + square(this.y - o.y) + square(this.z - o.z);
	}
	
	public double square(double a) {
		return a * a;
	}
	
	public Point add(Vector vec) {
		x += vec.getX();
		y += vec.getY();
		z += vec.getZ();
		return this;
	}
	
	public Point sub(Vector vec) {
		x -= vec.getX();
		y -= vec.getY();
		z -= vec.getZ();
		return this;
	}
	
	@Override
	public Point clone() {
		try {
			return (Point) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "Point[x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	public String toShowableString() {
		return "Point[x: " + String.format("%.2f", this.x) + ", y: " + String.format("%.2f", this.y) + ", z: " + String.format("%.2f", this.z) + "]";
	}
}
