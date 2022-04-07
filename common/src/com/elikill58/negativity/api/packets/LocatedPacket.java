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
