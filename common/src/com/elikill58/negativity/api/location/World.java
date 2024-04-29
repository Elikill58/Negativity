package com.elikill58.negativity.api.location;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.elikill58.negativity.api.NegativityObject;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockHashMap;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.impl.server.block.EmptyBlock;

public abstract class World implements NegativityObject {

	private static final ConcurrentHashMap<String, World> worlds = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, World> getWorlds() {
		return worlds;
	}
	
	/**
	 * Get world instance
	 * 
	 * @param name the name of world
	 * @return the world or null if not loaded yet
	 */
	public static World getWorld(String name) {
		return worlds.get(name);
	}
	
	/**
	 * Get world instance
	 * 
	 * @param name the name of world
	 * @param worldFunction create the world that will be stored
	 * @return the world or null if not loaded yet
	 */
	public static World getWorld(String name, Function<String, World> worldFunction) {
		return worlds.computeIfAbsent(name, worldFunction);
	}
	
	protected final BlockHashMap content = new BlockHashMap(this);
	protected final EmptyBlock emptyBlock = new EmptyBlock(this);
	
	/**
	 * Get the world name
	 * 
	 * @return the world name
	 */
	public abstract String getName();

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Try to load from cache or get with {@link #getBlockAt0(int, int, int)}
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block or empty one
	 */
	public Block getBlockAt(int x, int y, int z) {
		return content.get(x, y, z);
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Try to load from cache or get with {@link #getBlockAt0(int, int, int)}
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block or empty one
	 */
	public Block getBlockAt(double x, double y, double z) {
		return content.get((int) x, (int) y, (int) z);
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Try to load from cache or get with {@link #getBlockAt0(int, int, int)}
	 * 
	 * @param v the block vector position
	 * @return the founded block or empty one
	 */
	public Block getBlockAt(Vector v) {
		return content.get(v);
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Try to load from cache or get with {@link #getBlockAt0(int, int, int)}
	 * 
	 * @param loc the block location
	 * @return the founded block or empty one
	 */
	public Block getBlockAt(Location loc) {
		return content.get(loc.toBlockVector());
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Can create error if world not loaded AND loading it async<br>
	 * Load directly from world
	 * 
	 * @param loc the block location
	 * @return the founded block or empty one
	 */
	public Block getBlockAt0(Location loc) {
		return getBlockAt0(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Can create error if world not loaded AND loading it async<br>
	 * Load directly from world
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block or empty one
	 */
	public abstract Block getBlockAt0(int x, int y, int z);
	
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
	
	public abstract List<Entity> getEntities();
	
	public abstract Optional<Entity> getEntityById(int id);

	public abstract List<Entity> getNearEntity(Location loc, double distance);
	
	public boolean isChunkLoaded(Location loc) {
		return isChunkLoaded(loc.getBlockX() / 16, loc.getBlockZ() / 16);
	}

	public abstract boolean isChunkLoaded(int chunkX, int chunkZ);

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof World))
			return false;
		World w = (World) obj;
		return (getName() != null && w.getName() != null && getName().equals(w.getName())) || super.equals(obj);
	}
}
