package com.elikill58.negativity.api.ray;

import java.util.HashMap;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public abstract class AbstractRay<T extends AbstractRayResult<?>> {

	protected final World w;
	protected final Location basePosition;
	protected final Vector vector;
	protected final int maxDistance;
	protected double lastDistance = 0;
	protected Location position;
	protected HashMap<Vector, Material> testedVec = new HashMap<>();
	
	protected AbstractRay(World w, Location position, Vector vector, int maxDistance) {
		this.w = w;
		this.position = position.clone();
		this.basePosition = position.clone();
		this.maxDistance = maxDistance;
		this.vector = vector.normalize();
	}
	
	/**
	 * Get world where is the ray action
	 * 
	 * @return world checked
	 */
	public World getWorld() {
		return w;
	}
	
	/**
	 * Get the begin point of the ray. It's used to check the distance between begin and actual ray.
	 * 
	 * @return the base position
	 */
	public Location getBasePosition() {
		return basePosition;
	}
	
	/**
	 * Get position of ray.
	 * The position will move while compilation
	 * 
	 * @return the position where is the ray
	 */
	public Location getPosition() {
		return position;
	}
	
	/**
	 * Direction of ray (rotation of entity by default
	 * 
	 * @return the direction vector
	 */
	public Vector getVector() {
		return vector;
	}
	
	/**
	 * Get all tested vector with the material (or a default one if block have not been get
	 * 
	 * @return vector and their material
	 */
	public HashMap<Vector, Material> getTestedVectors() {
		return testedVec;
	}
	
	/**
	 * Get the last distance
	 * 
	 * @return last distance
	 */
	public double getLastDistance() {
		return lastDistance;
	}
	
	/**
	 * Compile the block ray
	 * 
	 * @return the result of ray action
	 */
	public T compile() {
		RayResult ray;
		while(!(ray = next()).canFinish());
		return createResult(ray);
	}
	
	protected abstract T createResult(RayResult ray);
	
	/**
	 * Move to next block according to vector
	 * 
	 * @return the ray result of next block
	 */
	private RayResult next() {
		if(position.getBlockY() >= w.getMaxHeight())
			return RayResult.REACH_TOP;
		if(position.getBlockY() <= w.getMinHeight())
			return RayResult.REACH_BOTTOM;
		Location oldLoc = position.clone();
		Location loc = position.add(vector).clone();
		if(loc.getBlockX() != oldLoc.getBlockX()) { // if X change
			RayResult rs = tryLoc0(new Vector(loc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
				RayResult rsY = tryLoc0(new Vector(loc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
				if(rsY.canFinish())
					return rsY;
			}
		}
		if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
			RayResult rs = tryLoc0(new Vector(oldLoc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
				RayResult rsZ = tryLoc0(new Vector(oldLoc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				if(rsZ.canFinish())
					return rsZ;
			}
		}
		if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
			RayResult rs = tryLoc0(new Vector(oldLoc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockX() != oldLoc.getBlockX()) { // if Z change
				RayResult rsX = tryLoc0(new Vector(loc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
				if(rsX.canFinish())
					return rsX;
			}
			// already manage Z & X and Z & Y change before
		}
		return tryLoc0(loc.toBlockVector()); // if change but nothing found, check basic way
	}
	
	protected RayResult tryLoc0(Vector v) {
		if(testedVec.containsKey(v))
			return RayResult.CONTINUE;
		return tryLocation(v);
	}
	
	protected abstract RayResult tryLocation(Vector v);
}
