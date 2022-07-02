package com.elikill58.negativity.api.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LocationUtils {

	public static boolean isUsingElevator(Player p) {
		Location loc = p.getLocation().clone();
		Material m = loc.getBlock().getType();
		return m.getId().contains("WATER") || hasMaterialsAround(loc, "WATER", "BUBBLE");
	}
	
	public static boolean isBlockOfType(Location location, String... materials) {
		String blockMaterial = location.getBlock().getType().getId().toUpperCase(Locale.ROOT);
		for (String material : materials) {
			if (blockMaterial.contains(material.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if there is material around specified location
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms Material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialAround(Location loc, Material... ms) {
		List<Material> m = Arrays.asList(ms);
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
	 * Check if there is material around specified location
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param ms Material's name that we are searchingWarn: For 'REDSTONE', we will also find 'REDSTONE_BLOCK' and all other block with contains name ...
	 * @return true if one of specified material if around
	 */
	public static boolean hasMaterialsAround(Location loc, String... ms) {
		loc = loc.clone();
		if (isBlockOfType(loc, ms))
			return true;
		if (isBlockOfType(loc.add(0, 0, 1), ms))
			return true;
		if (isBlockOfType(loc.add(1, 0, 0), ms))
			return true;
		if (isBlockOfType(loc.add(0, 0, -1), ms))
			return true;
		if (isBlockOfType(loc.add(0, 0, -1), ms))
			return true;
		if (isBlockOfType(loc.add(-1, 0, 0), ms))
			return true;
		if (isBlockOfType(loc.add(-1, 0, 0), ms))
			return true;
		if (isBlockOfType(loc.add(0, 0, 1), ms))
			return true;
		if (isBlockOfType(loc.add(0, 0, 1), ms))
			return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThanExtended(Location loc, Material m) {
		return hasOtherThanExtended(loc, m.getId());
	}

	/**
	 * Check if there is other than material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThanExtended(Location loc, String m) {
		Location tempLoc = loc.clone();
		loc = loc.clone();
		if (!loc.getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		loc = tempLoc;
		if (!loc.add(0, 0, 2).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
				return true;
		if (!loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		return false;
	}

	/**
	 * Check if there is material around specified location.
	 * (2 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m the name that we are searching in material names
	 * @return true if one of specified material if around
	 */
	public static boolean hasExtended(Location loc, String m) {
		Location tempLoc = loc.clone();
		loc = loc.clone();
		if (loc.getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
			return true;
		loc = tempLoc;
		if (loc.add(0, 0, 2).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, -1).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(-1, 0, 0).getBlock().getType().getId().contains(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (loc.add(0, 0, 1).getBlock().getType().getId().contains(m))
				return true;
		if (loc.add(1, 0, 0).getBlock().getType().getId().contains(m))
			return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location.
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param m The material that we are searching
	 * @return true if one of specified material if around
	 */
	public static boolean hasOtherThan(Location loc, Material m) {
		loc = loc.clone();
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
			return true;
		return false;
	}

	/**
	 * Check if there is other than material around specified location.
	 * (1 block radius)
	 * 
	 * @param loc the location where you want to check
	 * @param name the name that we are searching in material names
	 * @return true if one of specified material is around
	 */
	public static boolean hasOtherThan(Location loc, String name) {
		loc = loc.clone();
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(0, 0, -1).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(-1, 0, 0).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(name))
			return true;
		if (!loc.add(0, 0, 1).getBlock().getType().getId().contains(name))
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
		
		for(Entity entity : world.getEntities()) {
			Location l = entity.getLocation();
			if (entity.getType().equals(EntityType.BOAT) && l.distance(loc) < 3)
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
		Location loc = p.getLocation().clone();
		int i = 0;
		while (!hasOtherThan(loc, "AIR") && i < 50) {
			loc.sub(0, 1, 0);
			i++;
		}
		return i;
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
				if (!type.equals(Materials.AIR) && hasMovingPosition(w, vector, vec3d, vec3d1)
						&& !type.getId().contains("WATER"))
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
					BlockFace direction;
					if ((d3 < d4) && (d3 < d5)) {
						direction = vecX > posX ? BlockFace.WEST : BlockFace.EAST;
						vec3d = new Vector(d0, vec3d.getY() + d7 * d3, vec3d.getZ() + d8 * d3);
					} else if (d4 < d5) {
						direction = vecY > posY ? BlockFace.DOWN : BlockFace.UP;
						vec3d = new Vector(vec3d.getX() + d6 * d4, d1, vec3d.getZ() + d8 * d4);
					} else {
						direction = vecZ > posZ ? BlockFace.NORTH : BlockFace.SOUTH;
						vec3d = new Vector(vec3d.getX() + d6 * d5, vec3d.getY() + d7 * d5, d2);
					}
					posX = UniversalUtils.floor(vec3d.getX()) - direction.getModX();
					posY = UniversalUtils.floor(vec3d.getY()) - direction.getModY();
					posZ = UniversalUtils.floor(vec3d.getZ()) - direction.getModZ();
					vector = new Location(w, posX, posY, posZ);
					if (!w.getBlockAt(vector).getType().equals(Materials.AIR)
							&& hasMovingPosition(w, vector, vec3d, vec3d1)) {
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
		if ((vec3minX != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3minX) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minX;
		if ((vec3maxX != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3maxX) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxX;
		if ((vec3minY != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3minY) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minY;
		if ((vec3maxY != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3maxY) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxY;
		if ((vec3minZ != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3minZ) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3minZ;
		if ((vec3maxZ != null)
				&& ((objDirection == null) || (vec3d.distanceSquared(vec3maxZ) < vec3d.distanceSquared(objDirection))))
			objDirection = vec3maxZ;
		return objDirection != null;
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

	public enum Direction {
		FRONT, BACK, LEFT, RIGHT, FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT
	}

	public static void teleportPlayerOnGround(Player p) {
		Location loc = p.getLocation().clone();
		while(loc.getBlock().getType().equals(Materials.AIR))
			loc.sub(0, 1, 0);
		p.teleport(loc);
	}

	public static boolean isInWater(Location loc) {
		return loc.getBlock().isLiquid() || loc.clone().add(0, -1, 0).getBlock().isLiquid()
				|| loc.clone().add(0, 1, 0).getBlock().isLiquid();
	}

	/**
	 * Get the arrow of the direction from the player to the given location
	 * 
	 * @param p the player which is requiring for direction
	 * @param loc the direction
	 * @return the arrow already parsed
	 */
	@Deprecated
	public static double getAngleTo(Player p, Location loc) {
		Location position = p.getLocation();
		Vector a = loc.clone().sub(position).toVector().normalize();
		Vector b = position.getDirection();
		double angle = Math.toDegrees(Math.acos(a.dot(b)));
		return (angle < 0) ? (360 + angle) : angle;
	}

	public static Direction getDirection(Player p, Location loc) {
		Location playerLocation = p.getLocation();
		Vector locVector = loc.toVector().subtract(playerLocation.toVector());
		
		double locAngle = Math.atan2(locVector.getZ(), locVector.getX());
		double playerAngle = Math.atan2(playerLocation.getDirection().getZ(), playerLocation.getDirection().getX());
		
		double angle = playerAngle - locAngle;
		
		while (angle > Math.PI) {
		    angle = angle - 2 * Math.PI;
		}
		
		while (angle < -Math.PI) {
		    angle = angle + 2 * Math.PI;
		}
		
		if (angle < -2.749 || angle >= 2.749) { // -7/8 pi
		    return Direction.BACK;
		} else if (angle < -1.963) { // -5/8 pi
		    return Direction.BACK_RIGHT;
		} else if (angle < -1.178) { // -3/8 pi
		    return Direction.RIGHT;
		} else if (angle < -0.393) { // -1/8 pi
		    return Direction.FRONT_RIGHT;
		} else if (angle < 0.393) { // 1/8 pi
		    return Direction.FRONT;
		} else if (angle < 1.178) { // 3/8 pi
		    return Direction.FRONT_LEFT;
		} else if (angle < 1.963) { // 5/8 p
		    return Direction.LEFT;
		} else if (angle < 2.749) { // 7/8 pi
		    return Direction.BACK_LEFT;
		}
		return null;
	}
}
