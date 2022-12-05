package com.elikill58.negativity.api.packets.packet.playout.deprecated;

import com.elikill58.negativity.api.block.data.ChunkData;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

/**
 * This packet should not well read informations. You should not use it.
 */
@Deprecated
public class NPacketPlayOutChunkData implements NPacketPlayOut {

	public ChunkData chunk;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.chunk = new ChunkData(serializer, version);
		this.chunk.read();
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.MAP_CHUNK;
	}
}
