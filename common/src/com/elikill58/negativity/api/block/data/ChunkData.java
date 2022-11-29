package com.elikill58.negativity.api.block.data;

import java.util.HashMap;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.block.palette.Palette;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ChunkData {

	public static final int MAX_BITS_PER_BLOCK = 8, CHUNK_HEIGHT = 256, CHUNK_WIDTH = 16, SECTION_HEIGHT = 16, SECTION_WIDTH = CHUNK_WIDTH,
			SECTION_VOLUME = (SECTION_HEIGHT * SECTION_WIDTH * SECTION_WIDTH), NUM_SECTIONS = 16;

	public int chunkX, chunkZ;
	/**
	 * Will be always null until NBT are not done in
	 * {@link PacketSerializer#readNBTTag()}
	 */
	public Object heightmaps;
	public HashMap<BlockPosition, Material> blocks = new HashMap<>();
	public HashMap<BlockPosition, Material> blockEntites = new HashMap<>();

	public ChunkData(PacketSerializer serializer, Version version) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		this.heightmaps = serializer.readNBTTag();
		byte[] data = serializer.readByteArray();
		serializer.readBytes(data);
		Adapter.getAdapter().debug("Data: " + data.length);
		if(data.length > 0)
			readChunkColumn(0, new PacketSerializer(Unpooled.copiedBuffer(data)), version);

		int amountEntites = serializer.readVarInt();
		for (int i = 0; i < amountEntites; i++) {
			byte xz = serializer.readByte();
			short y = serializer.readShort();
			int blockType = serializer.readVarInt();
			/* Object nbt = */serializer.readNBTTag();
			blockEntites.put(new BlockPosition((chunkX * 16) + ((xz >> 4) & 0xF), y, (chunkZ * 16) + (xz & 0xF)), version.getOrCreateNamedVersion().getMaterialForEntityBlock(blockType));
		}
	}

	private void readChunkColumn(int mask, PacketSerializer serializer, Version version) {
		for (int sectionY = 0; sectionY < (CHUNK_HEIGHT / SECTION_HEIGHT); sectionY++) {
			//if ((mask & (1 << sectionY)) != 0) { // Is the given bit set in the mask?
				byte bitsPerBlock = serializer.readByte();
				Palette palette = Palette.createPalette(bitsPerBlock);
				palette.read(serializer, version.getOrCreateNamedVersion());

				// A bitmask that contains bitsPerBlock set bits
				int individualValueMask = ((1 << bitsPerBlock) - 1);

				long[] dataArray = serializer.readLongArray();

				for (int y = 0; y < SECTION_HEIGHT; y++) {
					for (int z = 0; z < SECTION_WIDTH; z++) {
						for (int x = 0; x < SECTION_WIDTH; x++) {
							int blockNumber = (((y * SECTION_HEIGHT) + z) * SECTION_WIDTH) + x;
							int startLong = (blockNumber * bitsPerBlock) / 64;
							int startOffset = (blockNumber * bitsPerBlock) % 64;
							int endLong = ((blockNumber + 1) * bitsPerBlock - 1) / 64;

							long data;
							if (startLong == endLong) {
								data = (dataArray[startLong] >> startOffset);
							} else {
								int endOffset = 64 - startOffset;
								data = (dataArray[startLong] >> startOffset | dataArray[endLong] << endOffset);
							}
							data &= individualValueMask;

							// data should always be valid for the palette
							// If you're reading a power of 2 minus one (15, 31, 63, 127, etc...) that's out
							// of bounds,
							// you're probably reading light data instead

							Material state = palette.getStateForId((int) data);
							blocks.put(new BlockPosition(x + (chunkX * 16), y, z + (chunkZ * 16)), state);
						}
					}
				}
			//}
		}
	}

	public ChunkData(PacketSerializer serializer, Version version, int old) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		this.heightmaps = serializer.readNBTTag();
		byte[] data = serializer.readByteArray();
		ByteBuf chunkBuf = Unpooled.copiedBuffer(data);
		int blockCount = chunkBuf.readShort();
		readBlocks(new PacketSerializer(chunkBuf), version, 16 * 16 * 16, blockCount); // 4096
		// just ignore biome: not useful to read them
		// readBiomes(new PacketSerializer(chunkBuf), 4 * 4 * 4); // 94
		int amountEntites = serializer.readVarInt();
		for (int i = 0; i < amountEntites; i++) {
			byte xz = serializer.readByte();
			short y = serializer.readShort();
			int blockType = serializer.readVarInt();
			/* Object nbt = */serializer.readNBTTag();
			blockEntites.put(new BlockPosition((chunkX * 16) + ((xz >> 4) & 0xF), y, (chunkZ * 16) + (xz & 0xF)), version.getOrCreateNamedVersion().getMaterialForEntityBlock(blockType));
		}
	}

	private Block[] readBlocks(PacketSerializer buf, Version version, int amount, int limit) {
		NamedVersion nv = version.getOrCreateNamedVersion();
		int bitsPerEntry = buf.readUnsignedByte();
		if (bitsPerEntry == 0) { // single value for full chunk
			int blockState = buf.readVarInt();
			Material type = nv.getMaterial(blockState);
			// not sure about how to run over all values
			for (int x = (chunkX * 16); x < ((chunkX * 16) + 16); x++) {
				for (int z = (chunkZ * 16); z < ((chunkZ * 16) + 16); z++) {
					for (int y = -64; y < 320; y++) {
						blocks.put(new BlockPosition(x, y, z), type);
					}
				}
			}
		} else if (bitsPerEntry <= 8) { // indirect
			int bits = bitsPerEntry <= 4 ? 4 : bitsPerEntry;
			int amountOfThings = buf.readVarInt();
			int[] palette = new int[amountOfThings];
			for (int i = 0; i < amountOfThings; i++)
				palette[i] = buf.readVarInt();

			buf.readInt(bits);
			int dataLength = buf.readVarInt();
			if (dataLength > limit)
				Adapter.getAdapter().debug("Data length too high for limit: " + dataLength + " for " + limit);
			for (int i = 0; i < dataLength; i++) {
				long val = buf.readLong();
				long blockStateId = val >> 12;
				int x = (int) ((chunkX << 4) + (val >>> 8 & 0xF));
				int y = (int) ((0 << 4) + (val >>> 0 & 0xF));
				int z = (int) ((chunkZ << 4) + (val >>> 4 & 0xF));
				blocks.put(new BlockPosition(x, y, z), nv.getMaterial((int) blockStateId));
			}
		} else if (bitsPerEntry > 8) { // direct
			buf.readInt(15);
		} else {
			Adapter.getAdapter().debug("Incorrect bit entry " + bitsPerEntry);
		}
		return null;
	}
}
