package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInKeepAlive implements NPacketPlayIn {

	public long time;
	
	public NPacketPlayInKeepAlive() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		this.time = serializer.readVarInt();
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.KEEP_ALIVE;
	}
}
