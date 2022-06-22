package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUnset implements NPacketPlayIn {

	public final String packetName;
	
	public NPacketPlayInUnset(String packetName) {
		this.packetName = packetName;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.UNSET;
	}
}
