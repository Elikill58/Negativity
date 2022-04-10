package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;

public class NPacketPlayInPosition extends NPacketPlayInFlying {
	
	public NPacketPlayInPosition() {
		
	}

	public NPacketPlayInPosition(double x, double y, double z, float yaw, float pitch, boolean isGround) {
		super(x, y, z, yaw, pitch, isGround, true, false);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.POSITION;
	}
}
