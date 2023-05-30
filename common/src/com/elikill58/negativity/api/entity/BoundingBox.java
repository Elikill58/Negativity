package com.elikill58.negativity.api.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockChecker;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.maths.Point;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.logger.Debug;

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
	 * @param a
	 *            the first bounding box
	 * @param b
	 *            the second bounding box
	 * @param f
	 *            the function to know if we need min and max values
	 */
	public BoundingBox(BoundingBox a, BoundingBox b, BiFunction<Double, Double, Double> f) {
		this(f.apply(a.minX, b.minX), f.apply(a.minY, b.minY), f.apply(a.minZ, b.minZ), f.apply(a.maxX, b.maxX),
				f.apply(a.maxY, b.maxY), f.apply(a.maxZ, b.maxZ));
	}

	/**
	 * Get minimum X
	 * 
	 * @return min X
	 */
	public double getMinX() {
		return minX;
	}

	/**
	 * Get minimum Y
	 * 
	 * @return min Y
	 */
	public double getMinY() {
		return minY;
	}

	/**
	 * Get minimum Z
	 * 
	 * @return min Z
	 */
	public double getMinZ() {
		return minZ;
	}

	/**
	 * Get maximum X
	 * 
	 * @return max X
	 */
	public double getMaxX() {
		return maxX;
	}

	/**
	 * Get maximum Y
	 * 
	 * @return max Y
	 */
	public double getMaxY() {
		return maxY;
	}

	/**
	 * Get maximum Z
	 * 
	 * @return max Z
	 */
	public double getMaxZ() {
		return maxZ;
	}
	
	/**
	 * Create new bounding box with expanded values
	 * 
	 * @param size the size to add
	 * @return new bounding box
	 */
	public BoundingBox expand(double size) {
		return expand(size, size, size);
	}

	
	/**
	 * Create new bounding box with expanded values
	 * 
	 * @param x the x value to add
	 * @param y the y value to add
	 * @param z the z value to add
	 * @return new bounding box
	 */
	public BoundingBox expand(double x, double y, double z) {
		return new BoundingBox(minX - x, minY - y, minZ - z, maxX + x, maxY + y, maxZ + z);
	}

	
	/**
	 * Create new bounding box with reduced values
	 * 
	 * @param size the size to remove
	 * @return new bounding box
	 */
	public BoundingBox reduce(double size) {
		return reduce(size, size, size);
	}

	
	/**
	 * Create new bounding box with reduced values
	 * 
	 * @param x the x value to remove
	 * @param y the y value to remove
	 * @param z the z value to remove
	 * @return new bounding box
	 */
	public BoundingBox reduce(double x, double y, double z) {
		return new BoundingBox(minX + x, minY + y, minZ + z, maxX - x, maxY - y, maxZ - z);
	}
	
	public List<Point> getAllPoints(){
		List<Point> list = new ArrayList<>();
		list.add(new Point(minX, minY, minZ));
		list.add(new Point(minX, minY, maxZ));
		list.add(new Point(minX, maxY, minZ));
		list.add(new Point(minX, maxY, maxZ));
		list.add(new Point(maxX, maxY, minZ));
		list.add(new Point(maxX, minY, minZ));
		list.add(new Point(maxX, minY, maxZ));
		list.add(new Point(maxX, maxY, maxZ));
		return list;
	}

	/**
	 * Get minimum position as point
	 * 
	 * @return min point
	 */
	public Point getMin() {
		return new Point(minX, minY, minZ);
	}

	/**
	 * Get centered point (in X/Y/Z)
	 * 
	 * @return center mid point
	 */
	public Point getMid() {
		return new Point((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
	}

	/**
	 * Get maximum position as point
	 * 
	 * @return max point
	 */
	public Point getMax() {
		return new Point(maxX, maxY, maxZ);
	}

	public boolean isIn(Point p) {
		return isIn(p.x, p.y, p.z);
	}

	public boolean isCollide(BoundingBox bb) {
		return bb.getAllPoints().stream().filter(this::isIn).count() > 0; // at least one is in -> collide
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

	/**
	 * Get head point where does the attack comes from
	 * 
	 * @return the point of the player head
	 */
	public Point getAsHeadPoint() {
		return new Point((minX + maxX) / 2, maxY - 0.3, (minZ + maxZ) / 2);
	}
	
	/**
	 * Get intersection point between actual bounding box and the given player
	 * 
	 * @param p player that attack
	 * @return point of intersection or null if failed to find it
	 */
	public Point getIntersectPoint(Player p) {
		return getIntersectPoint(p.getBoundingBox().getAsHeadPoint(), p.getEyeLocation().getDirection());
	}
	
	/**
	 * Get intersection point between actual bounding box and given point/vector
	 * 
	 * @param begin the point where the attack comes from
	 * @param dir the vector where entity was looking at
	 * @return intersection point
	 */
	public Point getIntersectPoint(Point begin, Vector dir) {
		Point bb = begin.clone();
		Vector baseDir = dir.clone();
		if(dir.length() > 1)
			dir.normalize(); // make vector less than 1
		Point far = getMostFarPoint(begin);
		double porcent = 0.1, multiplyDir = 0.5;
		int cal = 1500;
		while (true) {
			Point current = begin.add(dir);
			if(isIn(current)) {// just enter
				if(porcent < 0.001)
					return current;
				begin.sub(dir); // go back to last one
				dir.multiply(porcent); // reduce dir
				porcent *= 0.5; // divide by 2 percent
			} else if(shouldBeStopped(bb, far, current)) { // too far
				current = begin.sub(dir); // go back to last one
				if(!shouldBeStopped(bb, far, current)) { // if should not be stopped
					porcent *= 0.5; // divide by 2 percent
					dir.multiply(multiplyDir); // reduce dir but not too much
				}
			} else {
				// now check if it's too far
				if(shouldBeStopped(bb.x, far.x, current.x)) { // check x
					Adapter.getAdapter().debug(Debug.GENERAL, "Return far for x");
					return null;
				}
				if(shouldBeStopped(bb.y, far.y, current.y)) { // check y
					Adapter.getAdapter().debug(Debug.GENERAL, "Return far for y");
					return null;
				}
				if(shouldBeStopped(bb.z, far.z, current.z)) { // check z
					Adapter.getAdapter().debug(Debug.GENERAL, "Return far for z");
					return null;
				}
				
			}
			cal--;
			if(cal <= 0) {
				// prevent stack overflow
				Adapter.getAdapter().debug(Debug.GENERAL, "Overflow ! Stopping for " + toString() + ", point: " + bb.toShowableString() + ", dir: " + baseDir.toString());
				Adapter.getAdapter().debug(Debug.GENERAL, "Current: " + current.toString() + ", dir: " + dir.toString());
				return null;
			}
		}
	}
	
	private boolean shouldBeStopped(Point bb, Point far, Point current) {
		return shouldBeStopped(bb.x, far.x, current.x) || shouldBeStopped(bb.y, far.y, current.y) || shouldBeStopped(bb.z, far.z, current.z); // if start upper, check if current lower or inverse
	}
	
	private boolean shouldBeStopped(double bb, double far, double current) {
		return ((bb > far && current < far) || (bb < far && current > far)); // if start upper, check if current lower or inverse
	}

	public Point getMostFarPoint(Point p) {
		Point res = null;
		double dis = 0;
		for(Point all : getAllPoints()) {
			double disAll = p.distance(all);
			if(disAll > dis) {
				res = all;
				dis = disAll;
			}
		}
		return res;
	}
	
	public BlockChecker getBlocks(World w) {
        List<Block> blocks = new ArrayList<>();

        for (double x = minX; x <= maxX; x++) {
            for (double y = minY; y <= maxY + 0.01; y++) {
                for (double z = minZ; z <= maxZ; z++) {
                    blocks.add(w.getBlockAt(x, y, z));
                }
            }
        }
        return new BlockChecker(blocks);
	}
	
	@Override
	public String toString() {
		return "BoundingBox[minX=" + fd(minX) + ",minY=" + fd(minY) + ",minZ=" + fd(minZ) + "/then/maxX=" + fd(maxX)
				+ ",maxY=" + fd(maxY) + ",maxZ=" + fd(maxZ) + "]";
	}

	/**
	 * Format double
	 * 
	 * @param d double to format
	 * @return formatted double for to string
	 */
	private String fd(double d) {
		return String.format("%.3f", d);
	}
	
	public double getWidth() {
		return maxX - minX;
	}
	
	public double getHeight() {
		return maxY - minY;
	}
	
	public double getDepth() {
		return maxZ - minZ;
	}
	
	/**
	 * Create new bounding box with moved position<br>
	 * This doesn't change the size of box, just move it
	 * 
	 * @param x move in X axis
	 * @param y move in Y axis
	 * @param z move in Z axis
	 * @return new box
	 */
	public BoundingBox move(double x, double y, double z) {
		return new BoundingBox(minX + x, minY + y, minZ + z, maxX + x, maxY + y, maxZ + z);
	}
}
