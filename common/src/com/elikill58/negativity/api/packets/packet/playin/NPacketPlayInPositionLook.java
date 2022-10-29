package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public class NPacketPlayInPositionLook extends NPacketPlayInFlying {

	public NPacketPlayInPositionLook() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		this.hasPos = true;
		this.hasLook = true;
		this.x = serializer.readDouble();
		this.y = serializer.readDouble();
		this.z = serializer.readDouble();
		this.yaw = serializer.readFloat();
		this.pitch = serializer.readFloat();
		super.read(serializer); // read ground value
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.POSITION_LOOK;
	}
}
