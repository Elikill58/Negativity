package com.elikill58.negativity.api.ray.block;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.ray.AbstractRayResult;
import com.elikill58.negativity.api.ray.RayResult;

public class BlockRayResult extends AbstractRayResult<BlockRay> {

	private final Block block;
	private final boolean hasBlockExceptSearched;
	
	protected BlockRayResult(BlockRay ray, RayResult rayResult, Block block, boolean hasBlockExceptSearched) {
		super(ray, rayResult);
		this.block = block;
		this.hasBlockExceptSearched = hasBlockExceptSearched;
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
	
	@Override
	public String toString() {
		return "block=" + block + "," + super.toString();
	}
}
