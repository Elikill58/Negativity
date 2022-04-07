package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutExplosion implements NPacketPlayOut, LocatedPacket {

	public double x, y, z;
	public Vector vec;
	
	public NPacketPlayOutExplosion() {
		
	}
	
	public NPacketPlayOutExplosion(double x, double y, double z, float vecX, float vecY, float vecZ) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.vec = new Vector(vecX, vecY, vecZ);
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
		return PacketType.Server.EXPLOSION;
	}
}
