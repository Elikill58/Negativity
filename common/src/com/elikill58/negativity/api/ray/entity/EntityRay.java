package com.elikill58.negativity.api.ray.entity;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.maths.Point;
import com.elikill58.negativity.api.ray.AbstractRay;
import com.elikill58.negativity.api.ray.RayResult;

public class EntityRay extends AbstractRay<EntityRayResult> {

	private final List<Entity> entities, foundedEntities = new ArrayList<>();
	private double lastDistance = 0;
	
	protected EntityRay(World w, Location position, Vector vector, int maxDistance, boolean onlyPlayers, List<Entity> bypassEntities) {
		super(w, position, vector, maxDistance);
		this.entities = new ArrayList<>(w.getEntities());
		this.entities.removeAll(bypassEntities);
		this.entities.removeIf(e -> !e.getWorld().equals(w));
		if(onlyPlayers)
			this.entities.removeIf((et) -> !et.getType().equals(EntityType.PLAYER));
	}
	
	/**
	 * Get all entities
	 * 
	 * @return all testing entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}
	
	@Override
	protected EntityRayResult createResult(RayResult ray) {
		return new EntityRayResult(this, ray, foundedEntities, lastDistance);
	}
	
	@Override
	protected RayResult tryLocation(Vector v) {
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
