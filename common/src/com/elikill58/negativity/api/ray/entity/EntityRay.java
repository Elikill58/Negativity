package com.elikill58.negativity.api.ray.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.maths.Point;
import com.elikill58.negativity.api.ray.RayResult;

public class EntityRay {

	private final World w;
	private final List<Entity> entities, foundedEntities = new ArrayList<>();
	private final Location basePosition;
	private final Vector vector;
	private final int maxDistance;
	private Location position;
	private double lastDistance = 0;
	private List<Vector> positions;
	private HashMap<Vector, Material> testedVec = new HashMap<>();
	
	protected EntityRay(World w, Location position, Vector vector, int maxDistance, boolean onlyPlayers, List<Entity> bypassEntities) {
		this.w = w;
		this.position = position.clone();
		this.basePosition = position.clone();
		this.maxDistance = maxDistance;
		this.vector = vector.normalize().divide(2);
		this.entities = new ArrayList<>(w.getEntities());
		this.entities.removeAll(bypassEntities);
		this.entities.removeIf(e -> !e.getWorld().equals(w));
		if(onlyPlayers)
			this.entities.removeIf((et) -> !et.getType().equals(EntityType.PLAYER));
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
	 * Get all entities
	 * 
	 * @return all testing entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}
	
	/**
	 * Get needed positions
	 * 
	 * @return Return an empty array if there is not any needed positions
	 */
	public List<Vector> getNeededPositions() {
		return positions;
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
	 * Compile the block ray
	 * 
	 * @return the result of ray action
	 */
	public EntityRayResult compile() {
		RayResult ray;
		while(!(ray = next()).canFinish());
		return new EntityRayResult(this, ray, foundedEntities, vector, lastDistance, testedVec);
	}
	
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
			RayResult rs = tryLoc(new Vector(loc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
				RayResult rsY = tryLoc(new Vector(loc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
				if(rsY.canFinish())
					return rsY;
			}
		}
		if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
			RayResult rs = tryLoc(new Vector(oldLoc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
				RayResult rsZ = tryLoc(new Vector(oldLoc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				if(rsZ.canFinish())
					return rsZ;
			}
		}
		if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
			RayResult rs = tryLoc(new Vector(oldLoc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockX() != oldLoc.getBlockX()) { // if Z change
				RayResult rsX = tryLoc(new Vector(loc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
				if(rsX.canFinish())
					return rsX;
			}
			// already manage Z & X and Z & Y change before
		}
		return tryLoc(loc.toBlockVector()); // if change but nothing found, check basic way
	}
	
	private RayResult tryLoc(Vector v) {
		if(testedVec.containsKey(v))
			return RayResult.CONTINUE;
		lastDistance = v.clone().distance(basePosition.toVector()); // check between both distance
		if(lastDistance >= maxDistance)
			return foundedEntities.isEmpty() ? RayResult.TOO_FAR : RayResult.NEEDED_FOUND; // Too far
		testedVec.put(v, Materials.STICK); // don't carrying of which block
		Point point = new Point(v);
		Point pointPos = new Point(position.toVector());
		for(Entity et : new ArrayList<>(entities)) {
			if(et.getBoundingBox().isIn(point)) {
				entities.remove(et);
				foundedEntities.add(et);
			} else {
				double dis = et.getLocation().distance(position);
				if(dis < 2) {
					double pointDis = et.getBoundingBox().getAllPoints().stream().mapToDouble(p -> p.distance(pointPos)).min().orElse(1);
					if(pointDis < 0.5) {
						entities.remove(et);
						foundedEntities.add(et);
					}
				}
			}
		}
		return entities.isEmpty() ? (foundedEntities.isEmpty() ? RayResult.NEEDED_NOT_FOUND : RayResult.NEEDED_FOUND) : RayResult.CONTINUE;
	}
}
