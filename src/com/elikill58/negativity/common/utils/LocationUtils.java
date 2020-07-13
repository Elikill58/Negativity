package com.elikill58.negativity.common.utils;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.location.Location;
import com.elikill58.negativity.common.location.Vector;
import com.elikill58.negativity.common.location.World;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LocationUtils {

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
				Location vector = Adapter.getAdapter().createLocation(w, posX, posY, posZ);
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
					vector = Adapter.getAdapter().createLocation(w, posX, posY, posZ);
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
}
