package com.elikill58.negativity.api.packets.packet.status;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketStatus;

public class NPacketStatusUnset implements NPacketStatus {

	
	public NPacketStatusUnset() {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Status.UNSET;
	}
}
