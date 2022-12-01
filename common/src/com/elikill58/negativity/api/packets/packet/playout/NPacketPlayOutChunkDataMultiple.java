package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.block.data.ChunkData;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutChunkDataMultiple implements NPacketPlayOut {

    private static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    private static final int BLOCKS_BYTES = BLOCKS_PER_SECTION * 2;
    private static final int LIGHT_BYTES = BLOCKS_PER_SECTION / 2;
    private static final int BIOME_BYTES = 16 * 16;
    
	public ChunkData[] chunks;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		boolean skyLight = serializer.readBoolean();
		int amount = serializer.readVarInt();
		this.chunks = new ChunkData[amount];
		for(int i = 0; i < amount; i++) {
			ChunkData c = new ChunkData(serializer, version);
			int bitMask = serializer.readUnsignedShort();
			byte[] data = new byte[Integer.bitCount(bitMask) * (BLOCKS_BYTES + (skyLight ? 2 : 1) * LIGHT_BYTES) + BIOME_BYTES];
			serializer.readBytes(data);
			c.deserializer1_8(skyLight, data, bitMask, true);
			this.chunks[i] = c;
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.MAP_CHUNK_BULK;
	}
}
