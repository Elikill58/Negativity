package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInPosition extends NPacketPlayInFlying {
	
	public NPacketPlayInPosition() {
		
	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.hasPos = true;
		this.hasLook = false;
		this.x = serializer.readDouble();
		this.y = serializer.readDouble();
		this.z = serializer.readDouble();
		super.read(serializer, version); // read ground value
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.POSITION;
	}
}
