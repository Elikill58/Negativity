package com.elikill58.negativity.api.ray.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.ray.RayResult;

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
		lastDistance = v.clone().distance(basePosition.toVector()); // check between both distance
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
		Material type = w.getBlockAt(v.clone()).getType();
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
}
