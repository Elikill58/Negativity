package com.elikill58.negativity.api.ray;

import java.util.HashMap;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Vector;

public abstract class AbstractRayResult<T extends AbstractRay<?>> {

	protected final T ray;
	protected final RayResult rayResult;
	protected final HashMap<Vector, Material> alltestedLoc;
	protected final Vector vec;
	
	protected AbstractRayResult(T ray, RayResult rayResult) {
		this.ray = ray;
		this.rayResult = rayResult;
		this.vec = ray.getVector();
		this.alltestedLoc = ray.getTestedVectors();
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
		return vec;
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
