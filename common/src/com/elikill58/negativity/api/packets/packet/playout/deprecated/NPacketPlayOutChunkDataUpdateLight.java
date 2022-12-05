package com.elikill58.negativity.api.packets.packet.playout.deprecated;

import com.elikill58.negativity.api.block.data.ChunkData;
import com.elikill58.negativity.api.block.data.LightData;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

/**
 * This packet should not well read informations. You should not use it.
 */
@Deprecated
public class NPacketPlayOutChunkDataUpdateLight implements NPacketPlayOut {

	public ChunkData chunk;
	public LightData light;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.chunk = new ChunkData(serializer, version);
		this.chunk.read();
		this.light = new LightData(serializer, version);
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.LEVEL_CHUNK_LIGHT;
	}
}
