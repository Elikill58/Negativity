package com.elikill58.negativity.api.ray.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.ray.AbstractRayBuilder;

/**
 * This class is already in work-in-progress. Do NOT use it yet.
 * 
 * @author Elikill58
 *
 * @deprecated don't use this yet
 */
@Deprecated
public class EntityRayBuilder extends AbstractRayBuilder<EntityRayBuilder, EntityRay> {

	private boolean onlyPlayer = false;
	private final List<Entity> bypassEntities = new ArrayList<>();
	private Entity searched = null;
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public EntityRayBuilder(Location position, @Nullable Entity entity) {
		super(position, entity);
		bypassEntities.add(entity);
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param p the player where the ray start
	 */
	public EntityRayBuilder(Player p) {
		super(p);
		bypassEntities.add(p);
	}
	
	/**
	 * Create a new BlockRayBuilder
	 * 
	 * @param position the started position of ray
	 * @param vector the direction vector
	 */
	public EntityRayBuilder(Location position, Vector vector) {
		super(position, vector);
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
	 * Change which entity are selected
	 * 
	 * @param b true if we are only looking for players
	 * @return this builder
	 */
	public EntityRayBuilder searched(Entity searched) {
		this.searched = searched;
		return this;
	}
	
	/**
	 * Build BlockRay<br>
	 * Warn: this method have to be runned as sync.
	 * 
	 * @return the block ray
	 */
	public EntityRay build() {
		return new EntityRay(w, position, vector, maxDistance, onlyPlayer, bypassEntities, searched);
	}
}
