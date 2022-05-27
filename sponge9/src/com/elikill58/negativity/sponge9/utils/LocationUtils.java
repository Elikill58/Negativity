package com.elikill58.negativity.sponge9.utils;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;
import org.spongepowered.api.world.server.ServerWorld;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3i;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge9.impl.location.SpongeWorld;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class LocationUtils {

	public static boolean hasLineOfSight(Player player, Location loc) {
		ServerWorld world = player.serverLocation().world();
		Vector3d eyePos = player.eyePosition().get();
		if (hasNaN(eyePos)) {
			return true;
		}
		
		Vector3d vec3d1 = new Vector3d(loc.getX(), loc.getY() + 1.74F, loc.getZ());
		if (hasNaN(vec3d1)) {
			return true;
		}
		
		int posX = UniversalUtils.floor(eyePos.x());
		int posY = UniversalUtils.floor(eyePos.y());
		int posZ = UniversalUtils.floor(eyePos.z());
		int vecX = UniversalUtils.floor(vec3d1.x());
		int vecY = UniversalUtils.floor(vec3d1.y());
		int vecZ = UniversalUtils.floor(vec3d1.z());
		Vector3i vector = new Vector3i(posX, posY, posZ);
		if (world.block(vector).type() != BlockTypes.AIR.get() && hasMovingPosition(vector, eyePos, vec3d1))
			return false;
		
		int i = 200;
		while (i-- >= 0) {
			if (hasNaN(eyePos))
				return true;

			if ((posX == vecX) && (posY == vecY) && (posZ == vecZ))
				return true;

			boolean movingX = true, movingY = true, movingZ = true;
			double d0 = 999.0D, d1 = 999.0D, d2 = 999.0D;
			double d3 = 999.0D, d4 = 999.0D, d5 = 999.0D;
			double d6 = vec3d1.x() - eyePos.x();
			double d7 = vec3d1.y() - eyePos.y();
			double d8 = vec3d1.z() - eyePos.z();
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
				d3 = (d0 - eyePos.x()) / d6;
				if (d3 == -0.0D)
					d3 = -1.0E-4D;
			}
			if (movingY) {
				d4 = (d1 - eyePos.y()) / d7;
				if (d4 == -0.0D)
					d4 = -1.0E-4D;
			}
			if (movingZ) {
				d5 = (d2 - eyePos.z()) / d8;
				if (d5 == -0.0D)
					d5 = -1.0E-4D;
			}
			Direction direction;
			if ((d3 < d4) && (d3 < d5)) {
				direction = vecX > posX ? Direction.WEST : Direction.EAST;
				eyePos = new Vector3d(d0, eyePos.y() + d7 * d3, eyePos.z() + d8 * d3);
			} else if (d4 < d5) {
				direction = vecY > posY ? Direction.DOWN : Direction.UP;
				eyePos = new Vector3d(eyePos.x() + d6 * d4, d1, eyePos.z() + d8 * d4);
			} else {
				direction = vecZ > posZ ? Direction.NORTH : Direction.SOUTH;
				eyePos = new Vector3d(eyePos.x() + d6 * d5, eyePos.y() + d7 * d5, d2);
			}
			posX = UniversalUtils.floor(eyePos.x()) - (direction == Direction.EAST ? 1 : 0);
			posY = UniversalUtils.floor(eyePos.y()) - (direction == Direction.UP ? 1 : 0);
			posZ = UniversalUtils.floor(eyePos.z()) - (direction == Direction.SOUTH ? 1 : 0);
			vector = new Vector3i(posX, posY, posZ);
			if (world.block(vector).type() != BlockTypes.AIR.get())
				if (hasMovingPosition(vector, eyePos, vec3d1))
					return false;
		}
		return true;
	}
	
	private static boolean hasNaN(Vector3d playerEyesPos) {
		return Double.isNaN(playerEyesPos.x()) || Double.isNaN(playerEyesPos.y()) || Double.isNaN(playerEyesPos.z());
	}
	
	private static boolean hasMovingPosition(Vector3i position, Vector3d vec3d, Vector3d vec3d1) {
		vec3d = vec3d.add(-position.x(), -position.y(), -position.z());
		vec3d1 = vec3d1.add(-position.x(), -position.y(), -position.z());
		Vector3d vec3minX = getVectorX(vec3d, vec3d1, 0);
		Vector3d vec3maxX = getVectorX(vec3d, vec3d1, 1);
		Vector3d vec3minY = getVectorY(vec3d, vec3d1, 0);
		Vector3d vec3maxY = getVectorY(vec3d, vec3d1, 1);
		Vector3d vec3minZ = getVectorZ(vec3d, vec3d1, 0);
		Vector3d vec3maxZ = getVectorZ(vec3d, vec3d1, 1);
		Vector3d objDirection = null;
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

	private static Vector3d getVectorX(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.x() - main.x();
		double d2 = vec1.y() - main.y();
		double d3 = vec1.z() - main.z();
		if (d1 * d1 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.x()) / d1;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector3d(main.x() + d1 * d4, main.y() + d2 * d4, main.z() + d3 * d4);
	}

	private static Vector3d getVectorY(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.x() - main.x();
		double d2 = vec1.y() - main.y();
		double d3 = vec1.z() - main.z();
		if (d2 * d2 < 1.0000000116860974E-7D)
			return null;
		double d4 = (paramDouble - main.y()) / d2;
		if ((d4 < 0.0D) || (d4 > 1.0D))
			return null;
		return new Vector3d(main.x() + d1 * d4, main.y() + d2 * d4, main.z() + d3 * d4);
	}

	private static Vector3d getVectorZ(Vector3d main, Vector3d vec1, double paramDouble) {
		double d1 = vec1.x() - main.x();
		double d2 = vec1.y() - main.y();
		double d3 = vec1.z() - main.z();
		if (d3 * d3 < 1.0000000116860974E-7D) {
			return null;
		}
		double d4 = (paramDouble - main.z()) / d3;
		if ((d4 < 0.0D) || (d4 > 1.0D)) {
			return null;
		}
		return new Vector3d(main.x() + d1 * d4, main.y() + d2 * d4, main.z() + d3 * d4);
	}
	
	public static Vector3d toSpongePosition(Location location) {
		return new Vector3d(location.getX(), location.getY(), location.getZ());
	}
	
	public static <E extends Entity> E createEntityAt(Location location, EntityType<E> entityType) {
		return ((ServerWorld) location.getWorld().getDefault()).createEntity(entityType, toSpongePosition(location));
	}
	
	public static ServerLocation toSponge(Location location) {
		return ServerLocation.of(((ServerWorld) location.getWorld().getDefault()), location.getX(), location.getY(), location.getZ());
	}
	
	public static Location toNegativity(ServerLocation location) {
		return new Location(new SpongeWorld(location.world()), location.x(), location.y(), location.z());
	}
	
	public static Location toNegativity(ServerWorld world, Vector3d vec) {
		return new Location(new SpongeWorld(world), vec.x(), vec.y(), vec.z());
	}
}
