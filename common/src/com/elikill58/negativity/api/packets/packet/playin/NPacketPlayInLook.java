package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInLook extends NPacketPlayInFlying {
	
	public NPacketPlayInLook() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.hasLook = true;
		this.hasPos = false;
		this.yaw = serializer.readFloat();
		this.pitch = serializer.readFloat();
		super.read(serializer, version); // read ground value
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.LOOK;
	}
}
