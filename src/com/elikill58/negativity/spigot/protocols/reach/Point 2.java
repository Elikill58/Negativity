package com.elikill58.negativity.spigot.protocols.reach;

public class Point {

	public double x, y, z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "Point: x=" + x + ", y=" + y + ", z=" + z;
	}
	
	public double distance(Point o) {
		return Math.sqrt(distanceSquared(o));
	}

	public double distanceSquared(Point o) {
		return square(this.x - o.x) + square(this.y - o.y) + square(this.z - o.z);
	}
	
	public double square(double a) {
		return a * a;
	}
}
