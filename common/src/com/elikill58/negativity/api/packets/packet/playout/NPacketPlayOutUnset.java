package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutUnset implements NPacketPlayOut {

	public NPacketPlayOutUnset() {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.UNSET;
	}
}
