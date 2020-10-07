package com.elikill58.negativity.api.ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockRay {
	
	private final World w;
	private final Location position;
	private final Vector vector;
	private final List<Material> filter, neededType;
	private int test;
	private boolean hasOther = false;
	
	protected BlockRay(World w, Location position, Vector vector, int maxTest, Material[] neededType, boolean ignoreAir, Material[] filter) {
		this.w = w;
		this.position = position.clone();
		this.test = maxTest;
		this.vector = new Vector(parseVector(vector.getX()), parseVector(vector.getY()), parseVector(vector.getZ()));
		this.neededType = neededType == null ? null : new ArrayList<>(Arrays.asList(neededType));
		this.filter = new ArrayList<>(Arrays.asList(filter));
		if(ignoreAir)
			this.filter.add(Materials.AIR);
	}
	
	private double parseVector(double d) {
		return (d < 1 && d > -1) ? d : (d > 0 ? 1 : -1);
	}
	
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
		test--;
		if(test == -1)
			return neededType != null ? RayResult.NEEDED_NOT_FOUND : RayResult.END_TRY;
		Block b = position.add(vector).getBlock();
		Material type = b.getType();
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
		private boolean ignoreAir = true;
		private Vector vector = Vector.ZERO;
		private int maxTest = 200;
		private Material[] filter = new Material[0], neededType = null;
		
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
		 * Edit the maximum amount of test
		 * 
		 * @param max Maximum test of ray
		 * @return this builder
		 */
		public BlockRayBuilder maxTest(int max) {
			this.maxTest = max;
			return this;
		}
		
		/**
		 * Build BlockRay
		 * 
		 * @return the block ray
		 */
		public BlockRay build() {
			return new BlockRay(w, position, vector, maxTest, neededType, ignoreAir, filter);
		}
	}

	public static enum RayResult {
		
		REACH_BOTTOM(true), REACH_TOP(true), NEEDED_FOUND(true), NEEDED_NOT_FOUND(true), END_TRY(true), END_FIND(true), CONTINUE(false);
		
		private final boolean canFinish;
		
		private RayResult(boolean canFinish) {
			this.canFinish = canFinish;
		}
		
		public boolean canFinish() {
			return canFinish;
		}
	}
}
