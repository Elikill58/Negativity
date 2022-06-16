package com.elikill58.negativity.api.ray.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.ray.AbstractRay;
import com.elikill58.negativity.api.ray.RayResult;

public class BlockRay extends AbstractRay<BlockRayResult> {
	
	private final List<Material> filter, neededType;
	private final RaySearch search;
	private boolean hasOther = false;
	private List<Vector> positions;
	
	protected BlockRay(World w, Location position, Vector vector, int maxDistance, Material[] neededType, RaySearch search, Material[] filter, List<Vector> positions) {
		super(w, position, vector, maxDistance);
		this.search = search;
		this.neededType = neededType == null ? null : new ArrayList<>(Arrays.asList(neededType));
		this.filter = new ArrayList<>(Arrays.asList(filter));
		this.positions = positions;
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
	
	@Override
	protected BlockRayResult createResult(RayResult ray) {
		return new BlockRayResult(this, ray, position.getBlock(), hasOther, lastDistance);
	}

	@Override
	protected RayResult tryLocation(Vector v) {
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
