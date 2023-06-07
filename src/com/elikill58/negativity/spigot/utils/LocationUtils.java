package com.elikill58.negativity.spigot.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.blocks.SpigotLocation;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LocationUtils {

	public static boolean isUsingElevator(Player p) {
		SpigotLocation loc = new SpigotLocation(p.getLocation());
		Material m = loc.getBlock().getType();
		return m.name().contains("WATER") || hasMaterialsAround(loc, "WATER", "BUBBLE");
	}

	/**
	 * Check if there is material around specified location (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms  Material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialAround(SpigotLocation loc, Material... ms) {
		loc = loc.clone();
		List<Material> m = Arrays.asList(ms);
		if (m.contains(loc.getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType()))
			return true;
		return false;
	}

	/**
	 * Check if there is material around specified location (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms  Material's name that we are searchingWarn: For 'REDSTONE', we will
	 *            also find 'REDSTONE_BLOCK' and all other block with contains name
	 *            ...
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialsAround(SpigotLocation loc, String... ms) {
		loc = loc.clone();
		if (blockIsOneOfThem(loc.getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		return false;
	}

	private static boolean blockIsOneOfThem(Block b, String... ms) {
		String type = b.getType().name().toUpperCase(Locale.ROOT);
		for (String s : ms)
			if (type.contains(s))
				return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location. (2 block
	 * radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m   The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThanExtended(SpigotLocation loc, Material m) {
		return hasOtherThanExtended(loc, m.name());
	}

	/**
	 * Check if there is other than material around specified location. (2 block
	 * radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m   the name that we are searching in material names
	 * @return true if one of specified material is around
	 */
	public static boolean hasOtherThanExtended(SpigotLocation loc, String m) {
		SpigotLocation tempLoc = loc.clone();
		loc = loc.clone();
		if (!loc.getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
			return true;
		loc = tempLoc;
		if (!loc.add(0, 0, 2).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, -1).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(-1, 0, 0).getBlock().getType().name().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, 1).getBlock().getType().name().contains(m))
				return true;
		if (!loc.add(1, 0, 0).getBlock().getType().name().contains(m))
			return true;
		return false;
	}

	/**
	 * Check if there is material around specified location. (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms  all name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasExtended(SpigotLocation loc, String... ms) {
		SpigotLocation tempLoc = loc.clone();
		loc = loc.clone();
		if (blockIsOneOfThem(loc.getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
			return true;
		loc = tempLoc;
		if (blockIsOneOfThem(loc.add(0, 0, 2).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), ms))
			return true;
		if (blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), ms))
			return true;
		for (int i = 0; i < 4; i++)
			if (blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), ms))
				return true;
		for (int i = 0; i < 4; i++)
			if (blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), ms))
				return true;
		for (int i = 0; i < 4; i++)
			if (blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), ms))
				return true;
		if (blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), ms))
			return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location. (1 block
	 * radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m   The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThan(SpigotLocation loc, Material m) {
		return hasOtherThan(loc, m.name());
	}

	/**
	 * Check if there is other than material around specified location. (1 block
	 * radius)
	 * 
	 * @param loc  the location where you want to check
	 * @param name the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThan(SpigotLocation loc, String... name) {
		loc = loc.clone();
		if (!blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(1, 0, 0).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(0, 0, -1).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(-1, 0, 0).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), name))
			return true;
		if (!blockIsOneOfThem(loc.add(0, 0, 1).getBlock(), name))
			return true;
		return false;
	}

	/**
	 * Check is there is a boat around the location (Distance of 3)
	 * 
	 * @param loc The location to check
	 * @return true if there is a boat
	 */
	public static boolean hasBoatAroundHim(Location loc) {
		World world = loc.getWorld();
		if (world == null) {
			return false;
		}

		for (Entity entity : world.getEntities()) {
			Location l = entity.getLocation();
			if (entity instanceof Boat && l.distance(loc) < 3)
				return true;
		}
		return false;
	}

	/**
	 * Get the number of air below the specified player
	 * 
	 * @param p the player to know how many air blocks he has below
	 * @return the number of air block below
	 */
	public static int getNbAirBlockDown(Player p) {
		SpigotLocation loc = new SpigotLocation(p.getLocation());
		int i = 0;
		while (!LocationUtils.hasOtherThan(loc, "AIR") && i < 50) {
			loc.subtract(0, 1, 0);
			i++;
		}
		return i;
	}

	/**
	 * Check if the player can see the entity
	 * 
	 * @param p           the player to check if can see the specified entity
	 * @param entityToSee the entity to check if it viewable
	 * @return true if the player can see the entity
	 */
	public static boolean canSeeEntity(Player p, Entity entityToSee) {
		return canSeeEntity(p, entityToSee, 150);
	}

	/**
	 * Check if the player can see the entity The number of task is multiplied by 10
	 * when we are near the entity, to be sure that we don't pass through it
	 * 
	 * @param p           the player to check if can see the specified entity
	 * @param entityToSee the entity to check if it viewable
	 * @param maxDistance the maximum distance. Limited to 1000
	 * @return true if the player can see the entity
	 */
	public static boolean canSeeEntity(Player p, Entity entityToSee, int maxDistance) {
		if (SpigotNegativity.isCraftBukkit)
			return true; // bugged "hasLineOfSight" for craftbukkit
		return p.hasLineOfSight(entityToSee);
	}

	/**
	 * Check if the player can see the location
	 * 
	 * @param p        the player to check if can see the specified location
	 * @param locToSee the location to check if it viewable
	 * @return true if the player can see the location
	 */
	public static boolean canSeeLocation(Player p, SpigotLocation locToSee) {
		return canSeeLocation(p, locToSee, 100);
	}

	/**
	 * Check if the player can see the location
	 * 
	 * @param p           the player to check if can see the specified location
	 * @param locToSee    the location to check if it viewable
	 * @param maxDistance the maximum distance. Limited to 200
	 * @return true if the player can see the location
	 */
	public static boolean canSeeLocation(Player p, SpigotLocation locToSee, int maxDistance) {
		Location loc = p.getLocation().clone().add(0, 1.5, 0);
		World w = p.getWorld();
		Vector vector = loc.getDirection().normalize().clone();
		if (maxDistance > 200)
			maxDistance = 200;
		for (int i = 0; i < maxDistance; i++) {
			loc.add(vector);
			Material type = w.getBlockAt(loc).getType();
			if (type.isSolid())
				return false;
			if (loc.getBlockX() == locToSee.getBlockX() && loc.getBlockY() == locToSee.getBlockY() && loc.getBlockZ() == locToSee.getBlockZ())
				return true;
		}
		return false;
	}

	public static float getPlayerHeadHeight(Player p) {
		float f = 1.62F;
		if (p.isSleeping()) {
			f = 0.2F;
		}
		if (p.isSneaking()) {
			f -= 0.08F;
		}
		return f;
	}

	public static Vector getPlayerVec(Player p) {
		Location loc = p.getLocation();
		return new Vector(loc.getX(), loc.getY() + getPlayerHeadHeight(p), loc.getZ());
	}

	/**
	 * Check if the player see the location. Please, use
	 * {@link LocationUtils#canSeeEntity(Player, Entity)} or
	 * {@link LocationUtils#canSeeEntity(Player, Entity, int)}}
	 * 
	 * @param p   the player who must to see the location
	 * @param loc the location that we want to see
	 * @return true if the player can see the location
	 */
	@Deprecated
	public static boolean hasLineOfSight(Player p, Location loc) {
		World w = p.getWorld();
		Vector vec3d = getPlayerVec(p), vec3d1 = new Vector(loc.getX(), loc.getY() + 1.74F, loc.getZ());
		if (!Double.isNaN(vec3d.getX()) && !Double.isNaN(vec3d.getY()) && !Double.isNaN(vec3d.getZ())) {
			if (!Double.isNaN(vec3d1.getX()) && !Double.isNaN(vec3d1.getY()) && !Double.isNaN(vec3d1.getZ())) {
				int posX = UniversalUtils.floor(vec3d.getX());
				int posY = UniversalUtils.floor(vec3d.getY());
				int posZ = UniversalUtils.floor(vec3d.getZ());
				int vecX = UniversalUtils.floor(vec3d1.getX());
				int vecY = UniversalUtils.floor(vec3d1.getY());
				int vecZ = UniversalUtils.floor(vec3d1.getZ());
				Location vector = new Location(w, posX, posY, posZ);
				Material type = w.getBlockAt(vector).getType();
				if (!type.equals(Material.AIR) && hasMovingPosition(w, vector, vec3d, vec3d1) && !type.name().contains("WATER"))
					return false;

				int i = 200;
				while (i-- >= 0) {
					if (Double.isNaN(vec3d.getX()) || Double.isNaN(vec3d.getY()) || Double.isNaN(vec3d.getZ()))
						return true;

					if ((posX == vecX) && (posY == vecY) && (posZ == vecZ))
						return true;

					boolean movingX = true, movingY = true, movingZ = true;
					double d0 = 999.0D, d1 = 999.0D, d2 = 999.0D;
					double d3 = 999.0D, d4 = 999.0D, d5 = 999.0D;
					double d6 = vec3d1.getX() - vec3d.getX();
					double d7 = vec3d1.getY() - vec3d.getY();
					double d8 = vec3d1.getZ() - vec3d.getZ();
					if (vecX > posX) {
						d0 = posX + 1.0D;
					} else if (vecX < posX) {
						d0 = posX + 0.0D;
					} else
						movingX = false;

					if (vecY > posY) {
						d1 = posY + 1.0D;
					} else if (vecY < posY) {
						d1 = posY + 0.0D;
					} else
						movingY = false;

					if (vecZ > posZ) {
						d2 = posZ + 1.0D;
					} else if (vecZ < posZ) {
						d2 = posZ + 0.0D;
					} else
						movingZ = false;

					if (movingX) {
						d3 = (d0 - vec3d.getX()) / d6;
						if (d3 == -0.0D)
							d3 = -1.0E-4D;
					}
					if (movingY) {
						d4 = (d1 - vec3d.getY()) / d7;
						if (d4 == -0.0D)
							d4 = -1.0E-4D;
					}
					if (movingZ) {
						d5 = (d2 - vec3d.getZ()) / d8;
						if (d5 == -0.0D)
							d5 = -1.0E-4D;
					}
					Direction direction;
					if ((d3 < d4) && (d3 < d5)) {
						direction = vecX > posX ? Direction.WEST : Direction.EAST;
						vec3d = new Vector(d0, vec3d.getY() + d7 * d3, vec3d.getZ() + d8 * d3);
					} else if (d4 < d5) {
						direction = vecY > posY ? Direction.DOWN : Direction.UP;
						vec3d = new Vector(vec3d.getX() + d6 * d4, d1, vec3d.getZ() + d8 * d4);
					} else {
						direction = vecZ > posZ ? Direction.NORTH : Direction.SOUTH;
						vec3d = new Vector(vec3d.getX() + d6 * d5, vec3d.getY() + d7 * d5, d2);
					}
					posX = UniversalUtils.floor(vec3d.getX()) - (direction == Direction.EAST ? 1 : 0);
					posY = UniversalUtils.floor(vec3d.getY()) - (direction == Direction.UP ? 1 : 0);
					posZ = UniversalUtils.floor(vec3d.getZ()) - (direction == Direction.SOUTH ? 1 : 0);
					vector = new Location(w, posX, posY, posZ);
					if (!w.getBlockAt(vector).getType().equals(Material.AIR) && hasMovingPosition(w, vector, vec3d, vec3d1)) {
						return false;
					}
				}
				return true;
			}
			return true;
		}
		return true;
	}

	protected static boolean hasMovingPosition(World world, Location position, Vector vec3d, Vector vec3d1) {
		vec3d = vec3d.add(new Vector(-position.getX(), -position.getY(), -position.getZ()));
		vec3d1 = vec3d1.add(new Vector(-position.getX(), -position.getY(), -position.getZ()));
		Vector vec3minX = getVectorX(vec3d, vec3d1, 0);
		Vector vec3maxX = getVectorX(vec3d, vec3d1, 1);
		Vector vec3minY = getVectorY(vec3d, vec3d1, 0);
		Vector vec3maxY = getVectorY(vec3d, vec3d1, 1);
		Vector vec3minZ = getVectorZ(vec3d, vec3d1, 0);
		Vector vec3maxZ = getVectorZ(vec3d, vec3d1, 1);
		Vector objDirection = null;
		if ((vec3minX != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minX) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minX;
		if ((vec3maxX != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxX) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxX;
		if ((vec3minY != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minY) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minY;
		if ((vec3maxY != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxY) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxY;
		if ((vec3minZ != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3minZ) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minZ;
		if ((vec3maxZ != null) && ((objDirection == null) || (vec3d.distanceSquared(vec3maxZ) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxZ;
		if (objDirection == null)
			return false;
		return true;
	}

	private static Vector getVectorX(Vector main, Vector vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d1 * d1 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getX()) / d1;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vector getVectorY(Vector main, Vector vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d2 * d2 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getY()) / d2;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vector getVectorZ(Vector main, Vector vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - main.getZ()) / d3;
		if ((d4 < 0.0D) || (d4 > 1.0D)) {
			return null;
		}
		return new Vector(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	public static enum Direction {
		NORTH, SOUTH, WEST, EAST, UP, DOWN;
	}

	public static boolean hasAntiKbBypass(Player p) {
		SpigotLocation loc = new SpigotLocation(p.getLocation());
		return isInWater(loc) || isInWeb(loc) || hasCeiling(p);
	}

	public static boolean isInWater(SpigotLocation loc) {
		return loc.getBlock().isLiquid() || loc.clone().add(0, -1, 0).getBlock().isLiquid() || loc.clone().add(0, 1, 0).getBlock().isLiquid();
	}

	public static boolean isInWeb(SpigotLocation loc) {
		return isInWebForLocation(loc) || isInWebForLocation(loc.clone().add(0, 1, 0));
	}

	private static boolean isInWebForLocation(SpigotLocation loc) {
		double x = loc.getX() - loc.getBlockX(), z = loc.getZ() - loc.getBlockZ();

		if (isWeb(loc.getBlock()))
			return true;
		else if (x < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.WEST)))
			return true;
		else if (x > 0.69 && isWeb(loc.getBlock().getRelative(BlockFace.EAST)))
			return true;
		else if (z < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.NORTH)))
			return true;
		else if (z > 0.69 && isWeb(loc.getBlock().getRelative(BlockFace.SOUTH)))
			return true;
		else if (x > 0.71 && z < 0.3 && isWeb(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH)))
			return true;
		else if (x > 0.71 && z > 0.71 && isWeb(loc.getBlock().getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH)))
			return true;
		else if (x < 0.31 && z > 0.71 && isWeb(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH)))
			return true;
		else if (x < 0.31 && z < 0.31 && isWeb(loc.getBlock().getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH)))
			return true;
		return false;
	}

	private static boolean isWeb(Block b) {
		return b.getType().equals(ItemUtils.WEB);
	}

	public static boolean hasCeiling(Player player) {
		Location loc = player.getLocation().clone().add(0, 2, 0);
		if (loc.getBlock().getType().isSolid())
			return true;
		else if (loc.getX() > 0.66 && loc.getBlock().getRelative(BlockFace.EAST).getType().isSolid())
			return true;
		else if (loc.getX() < -0.66 && loc.getBlock().getRelative(BlockFace.WEST).getType().isSolid())
			return true;
		else if (loc.getZ() > 0.66 && loc.getBlock().getRelative(BlockFace.SOUTH).getType().isSolid())
			return true;
		else if (loc.getZ() < -0.66 && loc.getBlock().getRelative(BlockFace.NORTH).getType().isSolid())
			return true;
		return false;
	}

	public static double distanceXZ(Location l1, Location l2) {
		return Math.sqrt(square(l1.getX() - l2.getX()) + square(l1.getZ() - l2.getZ()));
	}
	
	public static double square(double d) {
		return d * d;
	}
}
