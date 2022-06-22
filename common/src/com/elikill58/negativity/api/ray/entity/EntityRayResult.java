package com.elikill58.negativity.api.ray.entity;

import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.ray.AbstractRayResult;
import com.elikill58.negativity.api.ray.RayResult;

/**
 * This class is already in work-in-progress. Do NOT use it yet.
 * 
 * @author Elikill58
 *
 * @deprecated don't use this yet
 */
@Deprecated
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
		return "entities=" + entitiesFounded + (ray.getEntities().size() == 1 ? ",begin=" + ray.getEntities() : "") + "," + super.toString();
	}
}
