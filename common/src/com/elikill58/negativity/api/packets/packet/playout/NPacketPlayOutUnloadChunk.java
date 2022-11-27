package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutUnloadChunk implements NPacketPlayOut {

	public int x, y;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.x = serializer.readInt();
		this.y = serializer.readInt();
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.UNLOAD_CHUNK;
	}
}
