package com.elikill58.negativity.api.ray.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class EntityRayBuilder {

	private final World w;
	private final Location position;
	private Vector vector = Vector.ZERO;
	private int maxDistance = 10;
	private boolean onlyPlayer = false;
	private final List<Entity> bypassEntities = new ArrayList<>();
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public EntityRayBuilder(Location position, @Nullable Entity entity) {
		bypassEntities.add(entity);
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
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public EntityRayBuilder(Player p) {
		bypassEntities.add(p);
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
	public EntityRayBuilder(Location position, Vector vector) {
		this.position = position;
		this.w = position.getWorld();
		this.vector = vector;
	}
	
	/**
	 * Edit the vector which correspond to ray direction
	 * 
	 * @param vec the new direction vector
	 * @return this builder
	 */
	public EntityRayBuilder vector(Vector vec) {
		this.vector = vec;
		return this;
	}

	/**
	 * Change the max ray distance
	 * 
	 * @param max the max distance of ray
	 * @return this builder
	 */
	public EntityRayBuilder maxDistance(int max) {
		this.maxDistance = max;
		return this;
	}
	
	/**
	 * Change which entity are selected
	 * 
	 * @param b true if we are only looking for players
	 * @return this builder
	 */
	public EntityRayBuilder onlyPlayers(boolean b) {
		this.onlyPlayer = b;
		return this;
	}
	
	/**
	 * Add entity that bypass the ray
	 * 
	 * @param entities all entities that should bypass ray
	 * @return this builder
	 */
	public EntityRayBuilder bypassEntities(Entity... entities) {
		this.bypassEntities.addAll(Arrays.asList(entities));
		return this;
	}
	
	/**
	 * Build BlockRay<br>
	 * Warn: this method have to be runned as sync.
	 * 
	 * @return the block ray
	 */
	public EntityRay build() {
		return new EntityRay(w, position, vector, maxDistance, onlyPlayer, bypassEntities);
	}
}
