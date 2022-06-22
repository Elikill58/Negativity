package com.elikill58.negativity.api.ray;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

@SuppressWarnings("unchecked")
public abstract class AbstractRayBuilder<T extends AbstractRayBuilder<T, R>, R extends AbstractRay<? extends AbstractRayResult<?>>> {

	protected final World w;
	protected final Location position;
	protected Vector vector = Vector.ZERO;
	protected int maxDistance = 10;
	
	/**
	 * Create a new RayBuilder
	 * 
	 * @param position the started position of ray
	 * @param entity which will give the rotation (and so the vector)
	 */
	public AbstractRayBuilder(Location position, @Nullable Entity entity) {
		this.position = position.clone().add(0, 0.5, 0);
		this.w = position.getWorld();
		if(entity != null)
			this.vector = entity.getRotation();
	}
	
	/**
	 * Create a new RayBuilder
	 * 
	 * @param p the player where the ray start
	 */
	public AbstractRayBuilder(Player p) {
		//NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
		this.position = p.getEyeLocation();// p.getLocation().clone().add(0, (p.isSneaking() ? (np.isBedrockPlayer() ? 0.75 : 0.5) : 0.8), 0);
		this.w = position.getWorld();
		this.vector = p.getRotation();
	}
	
	/**
	 * Create a new RayBuilder
	 * 
	 * @param position the started position of ray
	 * @param vector the direction vector
	 */
	public AbstractRayBuilder(Location position, Vector vector) {
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
	public T vector(Vector vec) {
		this.vector = vec;
		return (T) this;
	}

	/**
	 * Change the max ray distance
	 * 
	 * @param max the max distance of ray
	 * @return this builder
	 */
	public T maxDistance(int max) {
		this.maxDistance = max;
		return (T) this;
	}
	
	/**
	 * Build BlockRay<br>
	 * Warn: this method have to be runned as sync.
	 * 
	 * @return the block ray
	 */
	public abstract R build();
}
