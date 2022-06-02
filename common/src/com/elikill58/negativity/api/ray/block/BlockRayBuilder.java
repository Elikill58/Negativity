package com.elikill58.negativity.api.ray.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.ray.block.BlockRay.RaySearch;

public class BlockRayBuilder {

	private final World w;
	private final Location position;
	private RaySearch search = RaySearch.TYPE_NOT_AIR;
	private Vector vector = Vector.ZERO;
	private int maxDistance = 10;
	protected Material[] filter = new Material[0], neededType = new Material[0];
	protected List<Vector> positions = new ArrayList<>();
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public BlockRayBuilder(Location position, @Nullable Entity entity) {
		if(entity instanceof Player) {
			Player p = (Player) entity;
			this.position = position.clone().add(0, (p.isSneaking() ? (NegativityPlayer.getNegativityPlayer(p).isBedrockPlayer() ? 1.75 : 1.5) : 1.8), 0);
		} else
			this.position = position.clone().add(0, 0.5, 0); // TODO manage all entities
		this.w = position.getWorld();
		if(entity != null)
			this.vector = entity.getRotation();
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param p the player where the ray start
	 */
	public BlockRayBuilder(Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		Location loc = np.lastLocations.size() < 2 ? p.getLocation() : np.lastLocations.get(np.lastLocations.size() - 2);
		this.position = loc.clone().add(0, (p.isSneaking() ? (np.isBedrockPlayer() ? 1.75 : 1.5) : 1.8), 0);
		this.w = position.getWorld();
		this.vector = p.getRotation();
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param vector the direction vector
	 */
	public BlockRayBuilder(Location position, Vector vector) {
		this.position = position;
		this.w = position.getWorld();
		this.vector = vector;
	}
	
	/**
	 * Say if we have to ignore air
	 * 
	 * @param air true if the ray ignore air blocks
	 * @return this builder
	 */
	public BlockRayBuilder ignoreAir(boolean air) {
		this.search = RaySearch.TYPE_NOT_AIR;
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
		this.search = RaySearch.TYPE_SPECIFIC;
		return this;
	}
	
	/**
	 * Edit the vector which correspond to ray direction
	 * 
	 * @param vec the new direction vector
	 * @return this builder
	 */
	public BlockRayBuilder vector(Vector vec) {
		this.vector = vec;
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
		this.search = RaySearch.POSITION;
		return this;
	}

	/**
	 * Change the max ray distance
	 * 
	 * @param max the max distance of ray
	 * @return this builder
	 */
	public BlockRayBuilder maxDistance(int max) {
		this.maxDistance = max;
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
