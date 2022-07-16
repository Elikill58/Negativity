package com.elikill58.negativity.spigot.blocks;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SpigotWorld {

	private static final ConcurrentHashMap<String, SpigotWorld> spigotWorlds = new ConcurrentHashMap<>();
	public static ConcurrentHashMap<String, SpigotWorld> getWorlds() {
		return spigotWorlds;
	}
	
	/**
	 * Get world instance
	 * 
	 * @param name the name of world
	 * @return the world or null if not loaded yet
	 */
	public static SpigotWorld getWorld(World w) {
		return spigotWorlds.computeIfAbsent(w.getName(), a -> new SpigotWorld(w));
	}
	
	private final BlockHashMap content = new BlockHashMap(this);
	private final World world;
	
	public SpigotWorld(World world) {
		this.world = world;
	}
	
	public World getWorld() {
		return world;
	}
	
	public String getName() {
		return world.getName();
	}
	
	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Try to load from cache or get with {@link #getBlockAt0(int, int, int)}
	 * 
	 * @param x The X block location
	 * @param y The Y block location
	 * @param z The Z block location
	 * @return the founded block
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
	 * @return the founded block
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
	 * @return the founded block
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
	 * @return the founded block
	 */
	public Block getBlockAt(SpigotLocation loc) {
		return content.get(loc.toBlockVector());
	}

	/**
	 * Get the block at the specified location on this world
	 * Return a block with AIR type if not found<br>
	 * Can create error if world not loaded AND loading it async<br>
	 * Load directly from world
	 * 
	 * @param loc the block location
	 * @return the founded block
	 */
	public Block getBlockAt0(SpigotLocation loc) {
		return world.getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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
	 * @return the founded block
	 */
	public Block getBlockAt0(int x, int y, int z) {
		return world.getBlockAt(x, y, z);
	}
	
	/**
	 * Get all entities on this world
	 * 
	 * @return collection of world entities
	 */
	@Deprecated
	public List<Entity> getEntities() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof SpigotWorld))
			return false;
		SpigotWorld w = (SpigotWorld) obj;
		return getName().equals(w.getName());
	}
}
