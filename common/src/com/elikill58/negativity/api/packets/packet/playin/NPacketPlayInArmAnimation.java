package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInArmAnimation implements NPacketPlayIn {

	public long timestamp;
	
	public NPacketPlayInArmAnimation() {
		
	}
	
	public NPacketPlayInArmAnimation(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.ARM_ANIMATION;
	}
}
