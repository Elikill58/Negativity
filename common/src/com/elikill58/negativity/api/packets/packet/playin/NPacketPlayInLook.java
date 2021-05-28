package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;

public class NPacketPlayInLook extends NPacketPlayInFlying {
	
	public NPacketPlayInLook() {
		
	}

	public NPacketPlayInLook(double x, double y, double z, float yaw, float pitch, boolean isGround) {
		super(x, y, z, yaw, pitch, isGround, false, true);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.LOOK;
	}
}
