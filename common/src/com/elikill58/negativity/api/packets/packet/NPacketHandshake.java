package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public interface NPacketHandshake extends NPacket {

	@Override
	default void read(PacketSerializer serializer, Version version) {
		// TODO Not supported yet
	}
}
