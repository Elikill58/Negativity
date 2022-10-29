package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public interface NPacketUnset extends NPacket {

	@Override
	default void read(PacketSerializer serializer) {
		
	}
	
	void setPacketName(String name);
	
	String getPacketName();

	void setPacketTypeCible(PacketType type);
	
	PacketType getPacketTypeCible();
}
