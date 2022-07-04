package com.elikill58.negativity.api.packets;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.universal.utils.Maths;

/**
 * 
 * Represent a packet that can be located.
 * <br>
 * It mostly include block/movement packets.
 * 
 * @author Elikill58
 *
 */
public interface LocatedPacket {

	/**
	 * Check if this packet has valid location
	 * 
	 * @return true if location is valid
	 */
	default boolean hasLocation() {
		return !(getBlockX() == 0 && getBlockY() == 0 && getBlockZ() == 0) || !(getBlockX() == -1 && getBlockY() == -1 && getBlockZ() == -1);
	}
	
	double getX();

	default int getBlockX() {
		return Maths.roundLoc(getX());
	}
	
	double getY();

	default int getBlockY() {
		return Maths.roundLoc(getY());
	}
	double getZ();

	default int getBlockZ() {
		return Maths.roundLoc(getZ());
	}
	
	default Location getLocation(World w) {
		return new Location(w, getX(), getY(), getZ());
	}
	
	default Block getBlock(World w) {
		return w.getBlockAt(getX(), getY(), getZ());
	}
}
