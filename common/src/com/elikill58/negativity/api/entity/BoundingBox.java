package com.elikill58.negativity.api.entity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import com.elikill58.negativity.api.maths.Point;

public class BoundingBox {

	private final double minX, minY, minZ;
	private final double maxX, maxY, maxZ;
	
	public BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	/**
	 * Create a new bounding box while computing two bounding box
	 * 
	 * @param a the first bounding box
	 * @param b the second bounding box
	 * @param f the function to know if we need min and max values
	 */
	public BoundingBox(BoundingBox a, BoundingBox b, BiFunction<Double, Double, Double> f) {
		this(f.apply(a.minX, b.minX), f.apply(a.minY, b.minY), f.apply(a.minZ, b.minZ),
				f.apply(a.maxX, b.maxX), f.apply(a.maxY, b.maxY), f.apply(a.maxZ, b.maxZ));
	}

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	public double getMinZ() {
		return minZ;
	}

	public double getMaxX() {
		return maxX;
	}

	public double getMaxY() {
		return maxY;
	}

	public double getMaxZ() {
		return maxZ;
	}

	public Point getMin() {
		return new Point(minX, minY, minZ);
	}
	
	public Point getMid() {
		return new Point((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}

	public Point getMax() {
		return new Point(maxX, maxY, maxZ);
	}

	public boolean isIn(double x, double y, double z) {
		return (Math.min(minX, maxX) <= x && x <= Math.max(minX, maxX))
				&& (Math.min(minY, maxY) <= y && y <= Math.max(minY, maxY))
				&& (Math.min(minZ, maxZ) <= z && z <= Math.max(minZ, maxZ));
	}
	
	public Point getNearestPoint(Point other) {
		double x = getNeareatValue(other.x, Arrays.asList(minX, maxX));
		double y = getNeareatValue(other.y, Arrays.asList(minY, maxY));
		double z = getNeareatValue(other.z, Arrays.asList(minZ, maxZ));
		return new Point(x, y, z);
	}
	
	private double getNeareatValue(Double to, List<Double> all) {
		return all.stream().min(Comparator.comparingInt(i -> Math.abs((int) (i - to)))).get();
	}

	@Override
	public String toString() {
		return "BoundingBox[minX=" + minX + ",minY=" + minY + ",minZ=" + minZ + ",then,maxX=" + maxX + ",maxY=" + maxY
				+ ",maxZ=" + maxZ + ",]";
	}
}
