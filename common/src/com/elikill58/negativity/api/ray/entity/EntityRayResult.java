package com.elikill58.negativity.api.ray.entity;

import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.ray.AbstractRayResult;
import com.elikill58.negativity.api.ray.RayResult;

public class EntityRayResult extends AbstractRayResult<EntityRay> {

	private final List<Entity> entitiesFounded;
	private final double lastDistance;
	
	protected EntityRayResult(EntityRay ray, RayResult rayResult, List<Entity> foundedEntities, double lastDistance) {
		super(ray, rayResult);
		this.entitiesFounded = foundedEntities;
		this.lastDistance = lastDistance;
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
	 * Get the distance between begin and end.
	 * 
	 * @return the distance
	 */
	public double getLastDistance() {
		return lastDistance;
	}
}
