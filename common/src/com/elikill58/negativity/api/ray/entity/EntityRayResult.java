package com.elikill58.negativity.api.ray.entity;

import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.RayResult;

public class EntityRayResult {

	private final EntityRay ray;
	private final RayResult rayResult;
	private final List<Entity> entitiesFounded;
	private final HashMap<Vector, Material> alltestedLoc;
	private final double lastDistance;
	private final Vector vec;
	
	protected EntityRayResult(EntityRay ray, RayResult rayResult, List<Entity> foundedEntities, Vector vec, double lastDistance, HashMap<Vector, Material> testedLoc) {
		this.ray = ray;
		this.rayResult = rayResult;
		this.vec = vec;
		this.entitiesFounded = foundedEntities;
		this.lastDistance = lastDistance;
		this.alltestedLoc = testedLoc;
	}

	/**
	 * Get BlockRay which does ray action
	 * 
	 * @return get ray
	 */
	public EntityRay getRay() {
		return ray;
	}
	
	/**
	 * Get the Ray result
	 * 
	 * @return the result of ray
	 */
	public RayResult getRayResult() {
		return rayResult;
	}
	
	/**
	 * Get all founded entities
	 * 
	 * @return list of all entites in ray
	 */
	public List<Entity> getEntitiesFounded() {
		return entitiesFounded;
	}
	
	/**
	 * Get the vector used to make the ray
	 * 
	 * @return the vector
	 */
	public Vector getVector() {
		return vec;
	}
	
	/**
	 * Get the distance between begin and end.
	 * 
	 * @return the distance
	 */
	public double getLastDistance() {
		return lastDistance;
	}
	
	/**
	 * Get ALL tested locations as vector with the material founded.
	 * 
	 * @return all locations with their materials
	 */
	public HashMap<Vector, Material> getAllTestedLoc() {
		return alltestedLoc;
	}
}
