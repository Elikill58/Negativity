package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public interface NPacketHandshake extends NPacket {

	@Override
	default void read(PacketSerializer serializer) {
		// TODO Not supported yet
	}
}
