package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutUnset implements NPacketPlayOut {

	public final String packetName;
	
	public NPacketPlayOutUnset(String packetName) {
		this.packetName = packetName;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.UNSET;
	}
}
