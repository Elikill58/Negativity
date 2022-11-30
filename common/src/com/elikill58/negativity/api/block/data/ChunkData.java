package com.elikill58.negativity.api.block.data;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_16;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_18;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_8;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.Unpooled;

public class ChunkData {

	public int chunkX, chunkZ;
	/**
	 * Appear with chunk remap (since 1.16)
	 */
	public Object heightmaps;
	public HashMap<BlockPosition, Material> blocks = new HashMap<>();
	/**
	 * This field seems to don't be present for 1.8
	 */
	public HashMap<BlockPosition, Material> blockEntites = new HashMap<>();

	public ChunkData(PacketSerializer serializer, Version version) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		if (version.isNewerOrEquals(Version.V1_18)) {
			read1_18(serializer, version);
		} else if (version.isNewerOrEquals(Version.V1_16)) { // for 1.16 & 1.17
			read1_16(serializer, version);
		} else if (version.isNewerOrEquals(Version.V1_13)) {
			read1_13(serializer, version);
		} else if (version.isNewerOrEquals(Version.V1_9)) {
			read1_9(serializer, version);
		} else { // for older versions
			read1_8(serializer, version);
		}
	}

	private void readBlockEntities(PacketSerializer serializer, Version version) {
		int amountEntites = serializer.readVarInt();
		for (int i = 0; i < amountEntites; i++) {
			byte xz = serializer.readByte();
			short y = serializer.readShort();
			int blockType = serializer.readVarInt();
			try {
				/* Object nbt = */serializer.readNBTTag();
			} catch (Exception e) {
				Adapter.getAdapter().debug("Failed to read NBT: " + e.getMessage());
				return;
			}
			blockEntites.put(new BlockPosition((chunkX * 16) + ((xz >> 4) & 0xF), y, (chunkZ * 16) + (xz & 0xF)), version.getOrCreateNamedVersion().getMaterialForEntityBlock(blockType));
		}
	}

	private void read1_18(PacketSerializer serializer, Version version) {
		NamedVersion nv = version.getOrCreateNamedVersion();
		this.heightmaps = serializer.readNBTTag();
		PacketSerializer sectionsBuf = new PacketSerializer(serializer.readBytes(serializer.readVarInt()));
		for (int i = 0; i < 16; i++) {
			ChunkSection section = ChunkSectionReader1_18.read(sectionsBuf, version);
			int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
			for (int j = 0; j < values.length; j++) {
				int x = j % 16, y = j / 256 + (i * 16), z = j / 16 % 16;

				blocks.put(new BlockPosition(x + (chunkX * 16), y, z + (chunkZ * 16)), nv.getMaterial(values[j]));
			}
		}
		readBlockEntities(serializer, version);
	}

	private void read1_16(PacketSerializer serializer, Version version) {
		boolean fullChunk = serializer.readBoolean();
		serializer.readBoolean(); // ignore old light
		int primaryBitmask = serializer.readVarInt();
		this.heightmaps = serializer.readNBTTag();

		int[] biomeData = fullChunk ? new int[1024] : null;
		if (fullChunk) {
			for (int i = 0; i < 1024; i++) {
				biomeData[i] = serializer.readInt();
			}
		}
		serializer.readVarInt(); // data size

		for (int i = 0; i < 16; i++) {
			if ((primaryBitmask & (1 << i)) == 0)
				continue; // Section not set

			short nonAirBlocksCount = serializer.readShort();
			ChunkSection section = ChunkSectionReader1_16.read(serializer, version);
			section.setNonAirBlocksCount(nonAirBlocksCount);
			// section.forBlocks(chunkX, i, chunkZ, (pos, id) -> blocks.put(pos,
			// nv.getMaterial(id)));
		}
		readBlockEntities(serializer, version);
	}

	private void read1_13(PacketSerializer serializer, Version version) {

	}

	private void read1_9(PacketSerializer serializer, Version version) {

	}

	private void read1_8(PacketSerializer serializer, Version version) {
		boolean fullChunk = serializer.readBoolean();
		int bitmask = serializer.readUnsignedShort();
		int dataLength = serializer.readVarInt();
		byte[] data = new byte[dataLength];
		serializer.readBytes(data);
		if (fullChunk && bitmask == 0)
			return;
		PacketSerializer input = new PacketSerializer(Unpooled.wrappedBuffer(data));
		
		NamedVersion nv = version.getOrCreateNamedVersion();
		// Read blocks
		for (int i = 0; i < 16; i++) {
			if ((bitmask & 1 << i) == 0)
				continue;
			ChunkSection section = ChunkSectionReader1_8.read(input, version);
			int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
			Adapter.getAdapter().debug("Set " + values.length + " values for " + i);
			for (int j = 0; j < values.length; j++) {
				int x = j % 16, y = j, z = j / 16 % 16;
				if(i == 0)
					Adapter.getAdapter().debug("Set " + values[j] + " at " + (x + (chunkX * 16)) + "/" + (y / 256 + (i * 16)) + "/" + (z + (chunkZ * 16)));
				blocks.put(new BlockPosition(x + (chunkX * 16), y + (i * 16), z + (chunkZ * 16)), nv.getMaterial(values[j]));
			}
		}

		// Read block light
		/*
		 * for (int i = 0; i < sections.length; i++) { if ((bitmask & 1 << i) == 0)
		 * continue; sections[i].getLight().readBlockLight(input.getBuf()); }
		 * 
		 * // Read sky light if (skyLight) { // should check for world environment for
		 * (int i = 0; i < sections.length; i++) { if ((bitmask & 1 << i) == 0)
		 * continue; sections[i].getLight().readSkyLight(input.getBuf()); } }
		 * 
		 * // Read biome data if (fullChunk) { biomeData = new int[256]; for (int i = 0;
		 * i < 256; i++) { biomeData[i] = input.readUnsignedByte(); } } input.release();
		 */
	}
}
