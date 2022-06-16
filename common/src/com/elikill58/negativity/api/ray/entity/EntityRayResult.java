package com.elikill58.negativity.api.ray.entity;

import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.ray.AbstractRayResult;
import com.elikill58.negativity.api.ray.RayResult;

public class EntityRayResult extends AbstractRayResult<EntityRay> {

	private final List<Entity> entitiesFounded;
	
	protected EntityRayResult(EntityRay ray, RayResult rayResult, List<Entity> foundedEntities) {
		super(ray, rayResult);
		this.entitiesFounded = foundedEntities;
	}
	
	/**
	 * Get all founded entities
	 * 
	 * @return list of all entites in ray
	 */
	public List<Entity> getEntitiesFounded() {
		return entitiesFounded;
	}
	
	@Override
	public String toString() {
		return "entities=" + entitiesFounded + "," + super.toString();
	}
}
