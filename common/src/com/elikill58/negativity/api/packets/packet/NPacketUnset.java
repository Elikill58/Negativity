package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public interface NPacketUnset extends NPacket {

	@Override
	default void read(PacketSerializer serializer, Version version) {
		
	}
	
	void setPacketName(String name);

	@Override
	String getPacketName();

	void setPacketTypeCible(PacketType type);
	
	PacketType getPacketTypeCible();
}
