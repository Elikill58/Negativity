package com.elikill58.negativity.fabric.utils;

import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.minecraft.block.Material;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LocationUtils {

	public static boolean hasLineOfSight(ServerPlayerEntity p, World w, Vec3d loc) {
		Vec3d vec3d = Utils.getPlayerVec(p), vec3d1 = new Vec3d(loc.getX(), loc.getY() + 1.74F, loc.getZ());
		if (!Double.isNaN(vec3d.getX()) && !Double.isNaN(vec3d.getY()) && !Double.isNaN(vec3d.getZ())) {
			if (!Double.isNaN(vec3d1.getX()) && !Double.isNaN(vec3d1.getY()) && !Double.isNaN(vec3d1.getZ())) {
				int posX = UniversalUtils.floor(vec3d.getX());
				int posY = UniversalUtils.floor(vec3d.getY());
				int posZ = UniversalUtils.floor(vec3d.getZ());
				int vecX = UniversalUtils.floor(vec3d1.getX());
				int vecY = UniversalUtils.floor(vec3d1.getY());
				int vecZ = UniversalUtils.floor(vec3d1.getZ());
				Vec3d vector = new Vec3d(posX, posY, posZ);
				if (!w.getBlockState(new BlockPos(vector)).getMaterial().equals(Material.AIR) && hasMovingPosition(w, vector, vec3d, vec3d1))
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
						vec3d = new Vec3d(d0, vec3d.getY() + d7 * d3, vec3d.getZ() + d8 * d3);
					} else if (d4 < d5) {
						direction = vecY > posY ? Direction.DOWN : Direction.UP;
						vec3d = new Vec3d(vec3d.getX() + d6 * d4, d1, vec3d.getZ() + d8 * d4);
					} else {
						direction = vecZ > posZ ? Direction.NORTH : Direction.SOUTH;
						vec3d = new Vec3d(vec3d.getX() + d6 * d5, vec3d.getY() + d7 * d5, d2);
					}
					posX = UniversalUtils.floor(vec3d.getX()) - (direction == Direction.EAST ? 1 : 0);
					posY = UniversalUtils.floor(vec3d.getY()) - (direction == Direction.UP ? 1 : 0);
					posZ = UniversalUtils.floor(vec3d.getZ()) - (direction == Direction.SOUTH ? 1 : 0);
					vector = new Vec3d(posX, posY, posZ);
					if (!w.getBlockState(new BlockPos(vector)).getMaterial().equals(Material.AIR))
						if (hasMovingPosition(w, vector, vec3d, vec3d1))
							return false;
				}
				return true;
			}
			return true;
		}
		return true;
	}

	protected static boolean hasMovingPosition(World world, Vec3d position, Vec3d vec3d, Vec3d vec3d1) {
		vec3d = vec3d.add(-position.getX(), -position.getY(), -position.getZ());
		vec3d1 = vec3d1.add(-position.getX(), -position.getY(), -position.getZ());
		Vec3d vec3minX = getVectorX(vec3d, vec3d1, 0);
		Vec3d vec3maxX = getVectorX(vec3d, vec3d1, 1);
		Vec3d vec3minY = getVectorY(vec3d, vec3d1, 0);
		Vec3d vec3maxY = getVectorY(vec3d, vec3d1, 1);
		Vec3d vec3minZ = getVectorZ(vec3d, vec3d1, 0);
		Vec3d vec3maxZ = getVectorZ(vec3d, vec3d1, 1);
		Vec3d objDirection = null;
		if ((vec3minX != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3minX) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3minX;
		if ((vec3maxX != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3maxX) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3maxX;
		if ((vec3minY != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3minY) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3minY;
		if ((vec3maxY != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3maxY) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3maxY;
		if ((vec3minZ != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3minZ) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3minZ;
		if ((vec3maxZ != null)
				&& ((objDirection == null) || (vec3d.squaredDistanceTo(vec3maxZ) < vec3d.squaredDistanceTo(objDirection))))
			objDirection = vec3maxZ;
		return objDirection != null;
	}

	private static Vec3d getVectorX(Vec3d main, Vec3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d1 * d1 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getX()) / d1;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vec3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vec3d getVectorY(Vec3d main, Vec3d vec1, double paramDouble) {
		double d1 = vec1.getX() - main.getX();
		double d2 = vec1.getY() - main.getY();
		double d3 = vec1.getZ() - main.getZ();
		if (d2 * d2 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.getY()) / d2;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vec3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

	private static Vec3d getVectorZ(Vec3d main, Vec3d vec1, double paramDouble) {
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
		return new Vec3d(main.getX() + d1 * d4, main.getY() + d2 * d4, main.getZ() + d3 * d4);
	}

}
