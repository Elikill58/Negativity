package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUnset implements NPacketPlayIn {

	
	public NPacketPlayInUnset() {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.UNSET;
	}
}
