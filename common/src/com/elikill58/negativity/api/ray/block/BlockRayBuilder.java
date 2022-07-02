package com.elikill58.negativity.api.ray.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.AbstractRayBuilder;

public class BlockRayBuilder extends AbstractRayBuilder<BlockRayBuilder, BlockRay>{

	private BlockRaySearch search = BlockRaySearch.TYPE_NOT_AIR;
	protected Material[] filter = new Material[0], neededType = new Material[0];
	protected List<Vector> positions = new ArrayList<>();
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public BlockRayBuilder(Location position, @Nullable Entity entity) {
		super(position, entity);
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param p the player where the ray start
	 */
	public BlockRayBuilder(Player p) {
		super(p);
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param vector the direction vector
	 */
	public BlockRayBuilder(Location position, Vector vector) {
		super(position, vector);
	}
	
	/**
	 * Say if we have to ignore air
	 * 
	 * @param air true if the ray ignore air blocks
	 * @return this builder
	 */
	public BlockRayBuilder ignoreAir(boolean air) {
		this.search = BlockRaySearch.TYPE_NOT_AIR;
		return this;
	}
	
	/**
	 * Set a searched type.
	 * Empty materials
	 * 
	 * @param type all searched material
	 * @return this builder
	 */
	public BlockRayBuilder neededType(Material... type) {
		this.neededType = type;
		this.search = BlockRaySearch.TYPE_SPECIFIC;
		return this;
	}
	
	/**
	 * Edit the filter of ray (block which are ignored)
	 * 
	 * @param filter All transparent type for ray
	 * @return this builder
	 */
	public BlockRayBuilder filter(Material... filter) {
		this.filter = filter;
		return this;
	}
	
	/**
	 * Add searched position.
	 * 
	 * @param vec searched positions
	 * @return this builder
	 */
	public BlockRayBuilder neededPositions(Vector... vec) {
		return neededPositions(Arrays.asList(vec));
	}
	
	/**
	 * Add searched position.
	 * 
	 * @param vec searched positions
	 * @return this builder
	 */
	public BlockRayBuilder neededPositions(List<Vector> vec) {
		this.positions.addAll(vec);
		this.search = BlockRaySearch.POSITION;
		return this;
	}
	
	/**
	 * Build BlockRay<br>
	 * Warn: this method have to be runned as sync.
	 * 
	 * @return the block ray
	 */
	public BlockRay build() {
		if(search == null || !search.isValid(this))
			throw new IllegalArgumentException("Please check what you set as param before running ray.");
		return new BlockRay(w, position, vector, maxDistance, neededType, search, filter, positions);
	}
}
