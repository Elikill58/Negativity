package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.PacketType;

public class NPacketUnknown implements NPacket {


	@Override
	public PacketType getPacketType() {
		return PacketType.Client.UNSET;
	}
}
