package com.elikill58.negativity.sponge.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.vehicle.Boat;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

public class LocationUtils {


	public static boolean hasOtherThan(Location<?> loc, BlockType m) {
		try {
			if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
				return true;
			if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
				return true;
			if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
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
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean hasExtended(Location<World> loc, String m) {
		Location<World> tempLoc = loc.copy();
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

	public static boolean hasOtherThanExtended(Location<World> loc, BlockType m) {
		Location<World> tempLoc = loc.copy();
		if (!loc.getBlock().getType().equals(m))
			return true;
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
		loc = tempLoc;
		if (!loc.add(0, 0, 2).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, -1).getBlock().getType().equals(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(-1, 0, 0).getBlock().getType().equals(m))
				return true;
		for (int i = 0; i < 4; i++)
			if (!loc.add(0, 0, 1).getBlock().getType().equals(m))
				return true;
		if (!loc.add(1, 0, 0).getBlock().getType().equals(m))
			return true;
		return false;
	}

	public static boolean has(Location<?> loc, String... ms) {
		loc = loc.copy();
		List<String> m = Arrays.asList(ms);
		if (m.contains(loc.getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, -1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(-1, 0, 0).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		if (m.contains(loc.add(0, 0, 1).getBlock().getType().getId()))
			return true;
		return false;
	}
	
	public static boolean hasLineOfSight(Player p, Location<World> loc) {
		World w = p.getWorld();
		Vector3d vec3d = Utils.getPlayerVec(p), vec3d1 = new Vector3d(loc.getX(), loc.getY() + 1.74F, loc.getZ());
		if (!Double.isNaN(vec3d.getX()) && !Double.isNaN(vec3d.getY()) && !Double.isNaN(vec3d.getZ())) {
			if (!Double.isNaN(vec3d1.getX()) && !Double.isNaN(vec3d1.getY()) && !Double.isNaN(vec3d1.getZ())) {
				int posX = UniversalUtils.floor(vec3d.getX());
				int posY = UniversalUtils.floor(vec3d.getY());
				int posZ = UniversalUtils.floor(vec3d.getZ());
				int vecX = UniversalUtils.floor(vec3d1.getX());
				int vecY = UniversalUtils.floor(vec3d1.getY());
				int vecZ = UniversalUtils.floor(vec3d1.getZ());
				Vector3i vector = new Vector3i(posX, posY, posZ);
				if (!w.getBlock(vector).getType().equals(BlockTypes.AIR) && hasMovingPosition(w, vector, vec3d, vec3d1))
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
						vec3d = new Vector3d(d0, vec3d.getY() + d7 * d3, vec3d.getZ() + d8 * d3);
					} else if (d4 < d5) {
						direction = vecY > posY ? Direction.DOWN : Direction.UP;
						vec3d = new Vector3d(vec3d.getX() + d6 * d4, d1, vec3d.getZ() + d8 * d4);
					} else {
						direction = vecZ > posZ ? Direction.NORTH : Direction.SOUTH;
						vec3d = new Vector3d(vec3d.getX() + d6 * d5, vec3d.getY() + d7 * d5, d2);
					}
					posX = UniversalUtils.floor(vec3d.getX()) - (direction == Direction.EAST ? 1 : 0);
					posY = UniversalUtils.floor(vec3d.getY()) - (direction == Direction.UP ? 1 : 0);
					posZ = UniversalUtils.floor(vec3d.getZ()) - (direction == Direction.SOUTH ? 1 : 0);
					vector = new Vector3i(posX, posY, posZ);
					if (!w.getBlock(vector).getType().equals(BlockTypes.AIR))
						if(hasMovingPosition(w, vector, vec3d, vec3d1))
							return false;
				}
				return true;
			}
			return true;
		}
		return true;
	}
	
	protected static boolean hasMovingPosition(World world, Vector3i position, Vector3d vec3d, Vector3d vec3d1) {
		vec3d = vec3d.add(-position.getX(), -position.getY(), -position.getZ());
		vec3d1 = vec3d1.add(-position.getX(), -position.getY(), -position.getZ());
		Vector3d vec3minX = getVectorX(vec3d, vec3d1, 0);
		Vector3d vec3maxX = getVectorX(vec3d, vec3d1, 1);
		Vector3d vec3minY = getVectorY(vec3d, vec3d1, 0);
		Vector3d vec3maxY = getVectorY(vec3d, vec3d1, 1);
		Vector3d vec3minZ = getVectorZ(vec3d, vec3d1, 0);
		Vector3d vec3maxZ = getVectorZ(vec3d, vec3d1, 1);
		Vector3d objDirection = null;
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

	private static Vector3d getVectorX(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d1 * d1 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getX()) / d1;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vector3d getVectorY(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d2 * d2 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getY()) / d2;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vector3d getVectorZ(Vector3d main, Vector3d vec1, double paramDouble) {
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
		return new Vector3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	/**
	 * Check is there is a boat around the location (Distance of 3)
	 * 
	 * @param loc The location to check
	 * @return true if there is a boat
	 */
	public static boolean hasBoatAroundHim(Location<World> loc) {
		Collection<Entity> nearbyEntities = loc.getExtent().getNearbyEntities(loc.getPosition(), 3);
		for (Entity entity : nearbyEntities) {
			if (entity instanceof Boat) {
				return true;
			}
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
		Location<World> loc = p.getLocation();
		int i = 0;
		while (!LocationUtils.hasOtherThanExtended(loc, BlockTypes.AIR) && i < 20) {
			loc = loc.sub(Vector3i.UNIT_Y);
			i++;
		}
		return i;
	}

	public static boolean isUsingElevator(Player p) {
		Location<?> loc = p.getLocation().copy();
		BlockType m = loc.getBlock().getType();
		return m.getId().contains("WATER") || has(loc, "WATER", "BUBBLE");
	}
}
