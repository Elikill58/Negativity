package com.elikill58.negativity.api.ray;

import java.util.HashMap;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Vector;

public abstract class AbstractRayResult<T extends AbstractRay<?>> {

	protected final T ray;
	protected final RayResult rayResult;
	
	protected AbstractRayResult(T ray, RayResult rayResult) {
		this.ray = ray;
		this.rayResult = rayResult;
	}

	/**
	 * Get the ray object which does ray action
	 * 
	 * @return get ray
	 */
	public T getRay() {
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
	 * Get the vector used to make the ray
	 * 
	 * @return the vector
	 */
	public Vector getVector() {
		return ray.getVector();
	}
	
	/**
	 * Get ALL tested locations as vector with the material founded.
	 * 
	 * @return all locations with their materials
	 */
	public HashMap<Vector, Material> getAllTestedLoc() {
		return ray.getTestedVectors();
	}
	
	/**
	 * Get the last distance between begin and end.
	 * 
	 * @return last distance
	 */
	public double getLastDistance() {
		return ray.getLastDistance();
	}
	
	@Override
	public String toString() {
		return rayResult.name() + ",vec=" + getVector().toString() + ",lastDis=" + String.format("%.3f", getLastDistance()) + ",basePoc=" + ray.basePosition.toString();
	}
}
