package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInArmAnimation implements NPacketPlayIn {

	public long timestamp;
	
	public NPacketPlayInArmAnimation() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		 // not read from packet ?
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.ARM_ANIMATION;
	}
}
