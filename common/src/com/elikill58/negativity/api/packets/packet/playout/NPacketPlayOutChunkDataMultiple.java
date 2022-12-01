package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.block.chunks.Chunk;
import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_8;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.Unpooled;

public class NPacketPlayOutChunkDataMultiple implements NPacketPlayOut {

    private static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    private static final int BLOCKS_BYTES = BLOCKS_PER_SECTION * 2;
    private static final int LIGHT_BYTES = BLOCKS_PER_SECTION / 2;
    private static final int BIOME_BYTES = 16 * 16;
    
	public Chunk[] chunks;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		boolean skyLight = serializer.readBoolean();
		int amount = serializer.readVarInt();
		this.chunks = new Chunk[amount];
        final ChunkBulkSection[] chunkInfo = new ChunkBulkSection[amount];
		
        // Read metadata
        for (int i = 0; i < amount; i++) {
            chunkInfo[i] = new ChunkBulkSection(serializer, skyLight);
        }
        // Read data
		for(int i = 0; i < amount; i++) {
            ChunkBulkSection cbs = chunkInfo[i];
            Chunk c = new Chunk(cbs.chunkX, cbs.chunkZ);
			cbs.readData(serializer);
			readChunk(serializer, version, c, skyLight, cbs.getData(), cbs.bitmask, true);
			this.chunks[i] = c;
		}
	}
	
	private void readChunk(PacketSerializer serializer, Version version, Chunk c, boolean skyLight, byte[] data, int bitmask, boolean fullChunk) {
		PacketSerializer input = new PacketSerializer(serializer.getPlayer(), Unpooled.wrappedBuffer(data));

		NamedVersion nv = version.getNamedVersion();
		// Read blocks
		for (int i = 0; i < 16; i++) {
			if ((bitmask & 1 << i) == 0)
				continue;
			ChunkSection section = ChunkSectionReader1_8.read(input, version);
			int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
			for (int j = 0; j < values.length; j++) {
				int x = j % 16, y = j / 256, z = j / 16 % 16;
				c.set(x + (c.getX() * 16), y + (i * 16), z + (c.getZ() * 16), nv.getMaterial(values[j]));
			}
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.MAP_CHUNK_BULK;
	}

    public static final class ChunkBulkSection {
        private final int chunkX;
        private final int chunkZ;
        private final int bitmask;
        private final byte[] data;

        public ChunkBulkSection(PacketSerializer input, boolean skyLight) {
            this.chunkX = input.readInt();
            this.chunkZ = input.readInt();
            this.bitmask = input.readUnsignedShort();
            int bitMask = Integer.bitCount(this.bitmask);
            this.data = new byte[bitMask * (BLOCKS_BYTES + (skyLight ? 2 * LIGHT_BYTES : LIGHT_BYTES)) + BIOME_BYTES];
        }

        public void readData(PacketSerializer input) {
            input.readBytes(this.data);
        }

        public int getChunkX() {
            return this.chunkX;
        }

        public int getChunkZ() {
            return this.chunkZ;
        }

        public int getBitmask() {
            return this.bitmask;
        }

        public byte[] getData() {
            return this.data;
        }
    }
}
