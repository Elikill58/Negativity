package com.elikill58.negativity.api.ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;

public class BlockRay {
	
	private final World w;
	private final Location basePosition;
	private final Vector vector;
	private final List<Material> filter, neededType;
	private final int maxDistance;
	private final RaySearch search;
	private Location position;
	private boolean hasOther = false;
	private double lastDistance = 0;
	private List<Vector> positions;
	private HashMap<Vector, Material> testedVec = new HashMap<>();
	
	protected BlockRay(World w, Location position, Vector vector, int maxDistance, Material[] neededType, RaySearch search, Material[] filter, List<Vector> positions) {
		this.w = w;
		this.position = position.clone();
		this.basePosition = position.clone();
		this.maxDistance = maxDistance;
		this.search = search;
		this.vector = vector.normalize();// new Vector(parseVector(vector.getX()), parseVector(vector.getY()), parseVector(vector.getZ()));
		this.neededType = neededType == null ? null : new ArrayList<>(Arrays.asList(neededType));
		this.filter = new ArrayList<>(Arrays.asList(filter));
		this.positions = positions;
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
	 * Get the begin point of the ray. It's used to check the distance between begin and actual ray.
	 * 
	 * @return the base position
	 */
	public Location getBasePosition() {
		return basePosition;
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
	public List<Vector> getNeededPositions() {
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
		return new BlockRayResult(this, ray, position.getBlock(), hasOther, vector, lastDistance, testedVec);
	}
	
	/**
	 * Move to next block according to vector
	 * 
	 * @return the ray result of next block
	 */
	private RayResult next() {
		if(position.getBlockY() >= w.getMaxHeight())
			return RayResult.REACH_TOP;
		if(position.getBlockY() <= w.getMinHeight())
			return RayResult.REACH_BOTTOM;
		Location oldLoc = position.clone();
		Location loc = position.add(vector).clone();
		if(loc.getBlockX() != oldLoc.getBlockX()) { // if X change
			RayResult rs = tryLoc(new Vector(loc.getBlockX(), oldLoc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
				RayResult rsY = tryLoc(new Vector(loc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
				if(rsY.canFinish())
					return rsY;
			}
		}
		if(loc.getBlockY() != oldLoc.getBlockY()) { // if Y change
			RayResult rs = tryLoc(new Vector(oldLoc.getBlockX(), loc.getBlockY(), oldLoc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
				RayResult rsZ = tryLoc(new Vector(oldLoc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
				if(rsZ.canFinish())
					return rsZ;
			}
		}
		if(loc.getBlockZ() != oldLoc.getBlockZ()) { // if Z change
			RayResult rs = tryLoc(new Vector(oldLoc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
			if(rs.isFounded())
				return rs;
			if(loc.getBlockX() != oldLoc.getBlockX()) { // if Z change
				RayResult rsX = tryLoc(new Vector(loc.getBlockX(), oldLoc.getBlockY(), loc.getBlockZ()));
				if(rsX.canFinish())
					return rsX;
			}
			// already manage Z & X and Z & Y change before
		}
		return tryLoc(loc.toBlockVector()); // if change but nothing found, check basic way
	}
	
	private RayResult tryLoc(Vector v) {
		if(testedVec.containsKey(v))
			return RayResult.CONTINUE;
		lastDistance = v.distance(basePosition.toVector()); // check between both distance
		if(lastDistance >= maxDistance)
			return RayResult.TOO_FAR; // Too far
		testedVec.put(v, Materials.STICK); // will be replaced when getting from exact block
		if(search.equals(RaySearch.POSITION)) {
			int baseX = v.getBlockX(), baseY = v.getBlockY(), baseZ = v.getBlockZ();
			for(Vector vec : positions) {
				if(vec.getBlockX() == baseX && vec.getBlockY() == baseY && vec.getBlockZ() == baseZ) {
					position = new Location(w, vec.getX(), vec.getY(), vec.getZ());
					return RayResult.NEEDED_FOUND;
				}
			}
			return RayResult.CONTINUE;
		}
		Material type = w.getBlockAt(v).getType();
		testedVec.put(v, type); // changed tested type to the getted one
		if(search.equals(RaySearch.TYPE_SPECIFIC)) {
			if(neededType.contains(type)) {// founded type
				position = new Location(w, v.getX(), v.getY(), v.getZ());
				return RayResult.NEEDED_FOUND;
			}
			return RayResult.CONTINUE;
		} else if(search.equals(RaySearch.TYPE_NOT_AIR)) {
			if(!type.equals(Materials.AIR)) {
				position = new Location(w, v.getX(), v.getY(), v.getZ());
				return RayResult.NEEDED_FOUND;
			}
			return RayResult.CONTINUE;
		}
		return getFilter().contains(type) ? RayResult.CONTINUE : RayResult.FIND_OTHER;
	}
	
	public static class BlockRayBuilder {
		
		private final World w;
		private final Location position;
		private RaySearch search = RaySearch.TYPE_NOT_AIR;
		private Vector vector = Vector.ZERO;
		private int maxDistance = 10;
		private Material[] filter = new Material[0], neededType = new Material[0];
		private List<Vector> positions = new ArrayList<>();
		
		/**
		 * Create a new BlockRayBuilder
		 * 
		 * @param position the started position of ray
		 * @param entity which will give the rotation (and so the vector)
		 */
		public BlockRayBuilder(Location position, @Nullable Entity entity) {
			if(entity instanceof Player) {
				Player p = (Player) entity;
				this.position = position.clone().add(0, (p.isSneaking() ? (NegativityPlayer.getNegativityPlayer(p).isBedrockPlayer() ? 1.75 : 1.5) : 1.8), 0);
			} else
				this.position = position.clone().add(0, 0.5, 0); // TODO manage all entities
			this.w = position.getWorld();
			if(entity != null)
				this.vector = entity.getRotation();
		}
		
		/**
		 * Create a new BlockRayBuilder
		 * 
		 * @param position the started position of ray
		 * @param entity which will give the rotation (and so the vector)
		 */
		public BlockRayBuilder(Player p) {
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			Location loc = np.lastLocations.size() < 2 ? p.getLocation() : np.lastLocations.get(np.lastLocations.size() - 2);
			this.position = loc.clone().add(0, (p.isSneaking() ? (np.isBedrockPlayer() ? 1.75 : 1.5) : 1.8), 0);
			this.w = position.getWorld();
			this.vector = p.getRotation();
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
			this.search = RaySearch.TYPE_NOT_AIR;
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
			this.search = RaySearch.TYPE_SPECIFIC;
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
		public BlockRayBuilder neededPositions(Vector... vec) {
			return neededPositions(Arrays.asList(vec));
		}
		
		/**
		 * Add searched position.
		 * 
		 * @param loc searched positions
		 * @return this builder
		 */
		public BlockRayBuilder neededPositions(List<Vector> vec) {
			this.positions.addAll(vec);
			this.search = RaySearch.POSITION;
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
			if(search == null || !search.isValid(this))
				throw new IllegalArgumentException("Please check what you set as param before running ray.");
			return new BlockRay(w, position, vector, maxDistance, neededType, search, filter, positions);
		}
	}
	
	public enum RaySearch {
		
		POSITION(builder -> !builder.positions.isEmpty()),
		TYPE_SPECIFIC(builder -> builder.neededType != null && builder.neededType.length > 0),
		TYPE_NOT_AIR(builder -> true);
		
		private final Function<BlockRayBuilder, Boolean> checkIfValid;
		
		private RaySearch(Function<BlockRayBuilder, Boolean> checkIfValid) {
			this.checkIfValid = checkIfValid;
		}
		
		public boolean isValid(BlockRayBuilder builder) {
			return checkIfValid.apply(builder);
		}
	}

	public enum RayResult {
		
		REACH_BOTTOM(true, false, true),
		REACH_TOP(true, false, true),
		NEEDED_FOUND(true, true, true),
		NEEDED_NOT_FOUND(true, false, true),
		TOO_FAR(true, false, false),
		FIND_OTHER(true, false, false),
		CONTINUE(false, false, false);
		
		private final boolean canFinish, founded, shouldFinish;
		
		RayResult(boolean canFinish, boolean founded, boolean shouldFinish) {
			this.canFinish = canFinish;
			this.founded = founded;
			this.shouldFinish = shouldFinish;
		}
		
		public boolean canFinish() {
			return canFinish;
		}
		
		public boolean isFounded() {
			return founded;
		}
		
		public boolean isShouldFinish() {
			return shouldFinish;
		}
	}
}
