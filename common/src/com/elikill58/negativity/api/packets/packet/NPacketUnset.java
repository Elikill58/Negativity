package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.PacketType;

public interface NPacketUnset extends NPacket {

	void setPacketName(String name);
	
	String getPacketName();

	void setPacketTypeCible(PacketType type);
	
	PacketType getPacketTypeCible();
}
