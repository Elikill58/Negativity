package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInFlying implements NPacketPlayIn {

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
	public PacketType getPacketType() {
		return PacketType.Client.FLYING;
	}
}
