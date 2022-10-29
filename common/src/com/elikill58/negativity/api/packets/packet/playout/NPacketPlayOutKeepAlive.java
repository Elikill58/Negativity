package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutKeepAlive implements NPacketPlayOut {

	public long time;
	
	public NPacketPlayOutKeepAlive() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		this.time = serializer.readVarInt();
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.KEEP_ALIVE;
	}
}
