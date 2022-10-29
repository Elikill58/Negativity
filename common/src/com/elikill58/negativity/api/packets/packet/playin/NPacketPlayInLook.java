package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public class NPacketPlayInLook extends NPacketPlayInFlying {
	
	public NPacketPlayInLook() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		this.hasLook = true;
		this.hasPos = false;
		this.yaw = serializer.readFloat();
		this.pitch = serializer.readFloat();
		super.read(serializer); // read ground value
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.LOOK;
	}
}
