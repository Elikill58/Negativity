package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInFlying implements NPacketPlayIn, LocatedPacket {

	public double x, y, z;
	public float yaw, pitch;
	public boolean hasPos = false, hasLook = false, isGround = false;
	
	public NPacketPlayInFlying() {
		
	}

	public NPacketPlayInFlying(double x, double y, double z, float yaw, float pitch, boolean isGround, boolean hasPos, boolean hasLook) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.isGround = isGround;
		this.hasPos = hasPos;
		this.hasLook = hasLook;
	}
	
	@Override
	public boolean hasLocation() {
		return hasPos;
	}
	
	/**
	 * Warn: THIS CAN RETURN NULL.<br>
	 * If return a {@link Location} object but only according to {@link #hasPos} and {@link #hasLook} values.<br>
	 * If only has look, if will only return null because others values will be totally bugged
	 * 
	 * @param w the world on the location
	 * @return a location or null
	 */
	@Override
	public Location getLocation(World w) {
		if(hasPos && hasLook) // if fully move
			return new Location(w, x, y, z, yaw, pitch);
		else if(hasPos) // if just walk
			return new Location(w, x, y, z);
		else
			return null; // no real location
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.FLYING;
	}
}
