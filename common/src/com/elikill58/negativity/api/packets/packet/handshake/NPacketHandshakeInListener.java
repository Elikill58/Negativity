package com.elikill58.negativity.api.packets.packet.handshake;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Handshake;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;

public class NPacketHandshakeInListener implements NPacketHandshake {

	@Override
	public PacketType getPacketType() {
		return Handshake.IN_LISTENER;
	}

}
