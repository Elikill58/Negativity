package com.elikill58.negativity.api.ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockRay {
	
	private final World w;
	private final Location position, basePosition;
	private final Vector vector;
	private final List<Material> filter, neededType;
	private final int maxDistance;
	private boolean hasOther = false;
	private List<Location> positions;
	
	protected BlockRay(World w, Location position, Vector vector, int maxDistance, Material[] neededType, boolean ignoreAir, boolean ignoreEntity, Material[] filter, List<Location> positions) {
		this.w = w;
		this.position = position.clone();
		this.basePosition = position.clone();
		this.maxDistance = maxDistance;
		this.vector = vector.normalize();// new Vector(parseVector(vector.getX()), parseVector(vector.getY()), parseVector(vector.getZ()));
		this.neededType = neededType == null ? null : new ArrayList<>(Arrays.asList(neededType));
		this.filter = new ArrayList<>(Arrays.asList(filter));
		this.positions = positions;
		if(ignoreAir)
			this.filter.add(Materials.AIR);
		if(!ignoreEntity)
			w.getEntities().stream().map(Entity::getLocation).forEach(positions::add);
	}
	
	/*private double parseVector(double d) {
		return (d < 1 && d > -1) ? d : (d > 0 ? 1 : -1);
	}*/
	
	/**
	 * Get world where is the ray action
	 * 
	 * @return world checked
	 */
	public World getWorld() {
		return w;
	}
	
	/**
	 * Get position of ray.
	 * The position will move while compilation
	 * 
	 * @return the position where is the ray
	 */
	public Location getPosition() {
		return position;
	}
	
	/**
	 * Get filter (block which ray pass through)
	 * Contains AIR if "ignoreAir" is on true
	 * 
	 * @return list of filtered material
	 */
	public List<Material> getFilter() {
		return filter;
	}
	
	/**
	 * Get needed material
	 * Return null if you don't search specific material
	 * 
	 * @return searched type
	 */
	public List<Material> getNeededType() {
		return neededType;
	}
	
	/**
	 * Get needed positions
	 * 
	 * @return Return an empty array if there is not any needed positions
	 */
	public List<Location> getNeededPositions() {
		return positions;
	}
	
	/**
	 * Direction of ray (rotation of entity by default
	 * 
	 * @return the direction vector
	 */
	public Vector getVector() {
		return vector;
	}
	
	/**
	 * Compile the block ray
	 * 
	 * @return the result of ray action
	 */
	public BlockRayResult compile() {
		RayResult ray;
		while(!(ray = next()).canFinish());
		return new BlockRayResult(this, ray, position.getBlock(), hasOther);
	}
	
	/**
	 * Move to next block according to vector
	 * 
	 * @return the ray result of next block
	 */
	private RayResult next() {
		if(position.getBlockY() > 200)
			return RayResult.REACH_TOP;
		if(position.getBlockY() < 0)
			return RayResult.REACH_BOTTOM;
		Block b = position.add(vector).getBlock();
		double distance = position.distance(basePosition); // check between both distance
		if(distance >= maxDistance)
			return neededType != null ? RayResult.NEEDED_NOT_FOUND : RayResult.END_TRY;
		Material type = b.getType();
		if(!positions.isEmpty()) {
			int baseX = b.getX(), baseY = b.getY(), baseZ = b.getZ();
			for(Location vec : positions) {
				if(vec.getBlockX() == baseX && vec.getBlockY() == baseY && vec.getBlockZ() == baseZ) {
					return RayResult.NEEDED_FOUND;
				}
			}
		}
		if(neededType != null) {
			if(neededType.contains(type))
				return RayResult.NEEDED_FOUND;
			else if(!hasOther && !type.equals(Materials.AIR))
				hasOther = true;
			return RayResult.CONTINUE;
		} else {
			return getFilter().contains(type) ? RayResult.CONTINUE : RayResult.END_FIND;
		}
	}
	
	public static class BlockRayBuilder {
		
		private final World w;
		private final Location position;
		private boolean ignoreAir = true, ignoreEntity = true;
		private Vector vector = Vector.ZERO;
		private int maxDistance = 10;
		private Material[] filter = new Material[0], neededType = null;
		private List<Location> positions = new ArrayList<>();
		
		/**
		 * Create a new BlockRayBuilder
		 * 
		 * @param position the started position of ray
		 * @param entity which will give the rotation (and so the vector)
		 */
		public BlockRayBuilder(Location position, @Nullable Entity entity) {
			this.position = position;
			this.w = position.getWorld();
			if(entity != null)
				this.vector = entity.getRotation();
		}
		
		/**
		 * Create a new BlockRayBuilder
		 * 
		 * @param position the started position of ray
		 * @param vector the direction vector
		 */
		public BlockRayBuilder(Location position, Vector vector) {
			this.position = position;
			this.w = position.getWorld();
			this.vector = vector;
		}
		
		/**
		 * Say if we have to ignore air
		 * 
		 * @param air true if the ray ignore air blocks
		 * @return this builder
		 */
		public BlockRayBuilder ignoreAir(boolean air) {
			this.ignoreAir = air;
			return this;
		}
		
		/**
		 * Say if we have to ignore entity
		 * 
		 * @param entity true if the ray ignore entity
		 * @return this builder
		 */
		public BlockRayBuilder ignoreEntity(boolean entity) {
			this.ignoreEntity = entity;
			return this;
		}
		
		/**
		 * Set a searched type.
		 * Empty materials
		 * 
		 * @param type all searched material
		 * @return this builder
		 */
		public BlockRayBuilder neededType(Material... type) {
			this.neededType = type;
			return this;
		}
		
		/**
		 * Edit the vector which correspond to ray direction
		 * 
		 * @param vec the new direction vector
		 * @return this builder
		 */
		public BlockRayBuilder vector(Vector vec) {
			this.vector = vec;
			return this;
		}
		
		/**
		 * Edit the filter of ray (block which are ignored)
		 * 
		 * @param filter All transparent type for ray
		 * @return this builder
		 */
		public BlockRayBuilder filter(Material... filter) {
			this.filter = filter;
			return this;
		}
		
		/**
		 * Add searched position.
		 * 
		 * @param loc searched positions
		 * @return this builder
		 */
		public BlockRayBuilder neededPositions(Location... vec) {
			this.positions.addAll(Arrays.asList(vec));
			return this;
		}

		/**
		 * Change the max ray distance
		 * 
		 * @param max the max distance of ray
		 * @return this builder
		 */
		public BlockRayBuilder maxDistance(int max) {
			this.maxDistance = max;
			return this;
		}
		
		/**
		 * Build BlockRay<br>
		 * Warn: this method have to be runned as sync.
		 * 
		 * @return the block ray
		 */
		public BlockRay build() {
			return new BlockRay(w, position, vector, maxDistance, neededType, ignoreAir, ignoreEntity, filter, positions);
		}
	}

	public enum RayResult {
		
		REACH_BOTTOM(true), REACH_TOP(true), NEEDED_FOUND(true), NEEDED_NOT_FOUND(true), END_TRY(true), END_FIND(true), CONTINUE(false);
		
		private final boolean canFinish;
		
		RayResult(boolean canFinish) {
			this.canFinish = canFinish;
		}
		
		public boolean canFinish() {
			return canFinish;
		}
	}
}
