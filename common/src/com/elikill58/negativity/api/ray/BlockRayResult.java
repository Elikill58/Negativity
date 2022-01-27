package com.elikill58.negativity.api.ray;

import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.BlockRay.RayResult;

public class BlockRayResult {

	private final BlockRay ray;
	private final RayResult rayResult;
	private final Block block;
	private final boolean hasBlockExceptSearched;
	private final HashMap<Vector, Material> alltestedLoc;
	private final Vector vec;
	
	protected BlockRayResult(BlockRay ray, RayResult rayResult, Block block, boolean hasBlockExceptSearched, Vector vec, HashMap<Vector, Material> testedLoc) {
		this.ray = ray;
		this.rayResult = rayResult;
		this.block = block;
		this.hasBlockExceptSearched = hasBlockExceptSearched;
		this.vec = vec;
		this.alltestedLoc = testedLoc;
	}

	/**
	 * Get BlockRay which does ray action
	 * 
	 * @return get ray
	 */
	public BlockRay getRay() {
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
	 * Get final block which crate the end of ray
	 * 
	 * @return the block
	 */
	@Nullable
	public Block getBlock() {
		return block;
	}
	
	/**
	 * Get block type of final block
	 * 
	 * @return the type of block
	 */
	@Nullable
	public Material getType() {
		return block == null ? null : block.getType();
	}
	
	/**
	 * Know if there is block between location and block which is different than searched
	 * (Don't count AIR)
	 * Cannot be true if there isn't searched type
	 * 
	 * @return true if find other block than needed
	 */
	public boolean hasBlockExceptSearched() {
		return hasBlockExceptSearched;
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
