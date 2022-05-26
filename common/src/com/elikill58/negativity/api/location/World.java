package com.elikill58.negativity.api.location;

import java.util.List;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;

public abstract class World implements NegativityObject {

	/**
	 * Get the world name
	 * 
	 * @return the world name
	 */
	public abstract String getName();

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block
	 */
	public abstract Block getBlockAt(int x, int y, int z);

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block
	 */
	public Block getBlockAt(double x, double y, double z) {
		return getBlockAt((int) x, (int) y, (int) z);
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param loc the block location
	 * @return the founded block
	 */
	public Block getBlockAt(Vector v) {
		return getBlockAt(v.getBlockX(), v.getBlockY(), v.getBlockZ());
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found
	 * Can create error if world not loaded AND loading it async
	 * 
	 * @param loc the block location
	 * @return the founded block
	 */
	public abstract Block getBlockAt(Location loc);
	
	/**
	 * Get all entities on this world
	 * 
	 * @return collection of world entities
	 */
	public abstract List<Entity> getEntities();

	/**
	 * Get an entity by the ID
	 * 
	 * @param entityId the ID of the entity
	 * @return the founded entity or null if not is this world/no longer existing
	 */
	public @Nullable Entity getEntityWithID(int entityId) {
		for(Entity et : getEntities())
			if(et.isSameId(String.valueOf(entityId)))
				return et;
		return null;
	}
	
	/**
	 * Get the world difficulty
	 * 
	 * @return the world difficulty
	 */
	public abstract Difficulty getDifficulty();
	
	/**
	 * Get the max height of the world
	 * 
	 * @return the max height
	 */
	public abstract int getMaxHeight();
	
	/**
	 * Get the min height of the world
	 * 
	 * @return the min height
	 */
	public abstract int getMinHeight();
	
	/**
	 * Know if the pvp is enabled in this world.
	 * 
	 * @return true if pvp is enabled
	 */
	public abstract boolean isPVP();
}
