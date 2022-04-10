package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;

public class NPacketPlayInPositionLook extends NPacketPlayInFlying {

	public NPacketPlayInPositionLook() {
		
	}

	public NPacketPlayInPositionLook(double x, double y, double z, float yaw, float pitch, boolean isGround) {
		super(x, y, z, yaw, pitch, isGround, true, true);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.POSITION_LOOK;
	}
}
