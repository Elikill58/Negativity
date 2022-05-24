package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUnset implements NPacketPlayIn {

	public final String packetName;
	public final PacketType type;
	
	public NPacketPlayInUnset(String packetName, PacketType type) {
		this.packetName = packetName;
		this.type = type == null ? PacketType.Client.UNSET : type;
	}

	@Override
	public PacketType getPacketType() {
		return type;
	}
}
