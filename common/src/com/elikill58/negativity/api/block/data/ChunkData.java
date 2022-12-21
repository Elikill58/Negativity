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
import com.elikill58.negativity.universal.logger.Debug;

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
	
	private PacketSerializer serializer;
	private Version version;
	
	public ChunkData(PacketSerializer serializer, Version version) {
		this.serializer = serializer;
		this.version = version;
	}

	public void read() {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		if (version.isNewerOrEquals(Version.V1_18)) {
			read1_18();
		} else if (version.isNewerOrEquals(Version.V1_16)) { // for 1.16 & 1.17
			read1_16();
		} else if (version.isNewerOrEquals(Version.V1_13)) {
			read1_13();
		} else if (version.isNewerOrEquals(Version.V1_9)) {
			read1_9();
		} else { // for older versions
			read1_8(); // should check for world environment
		}
	}

	private void readBlockEntities() {
		int amountEntites = serializer.readVarInt();
		for (int i = 0; i < amountEntites; i++) {
			byte xz = serializer.readByte();
			short y = serializer.readShort();
			int blockType = serializer.readVarInt();
			try {
				/* Object nbt = */serializer.readNBTTag();
			} catch (Exception e) {
				Adapter.getAdapter().debug(Debug.BEHAVIOR, "Failed to read NBT: " + e.getMessage());
				return;
			}
			blockEntites.put(new BlockPosition((chunkX * 16) + ((xz >> 4) & 0xF), y, (chunkZ * 16) + (xz & 0xF)), version.getNamedVersion().getMaterialForEntityBlock(blockType));
		}
	}

	private void read1_18() {
		NamedVersion nv = version.getNamedVersion();
		this.heightmaps = serializer.readNBTTag();
		PacketSerializer sectionsBuf = new PacketSerializer(serializer.getPlayer(), serializer.readBytes(serializer.readVarInt()));
		for (int i = 0; i < 16; i++) {
			ChunkSection section = ChunkSectionReader1_18.read(sectionsBuf, version);
			int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
			for (int j = 0; j < values.length; j++) {
				int x = j % 16, y = j / 256 + (i * 16), z = j / 16 % 16;

				blocks.put(new BlockPosition(x + (chunkX * 16), y, z + (chunkZ * 16)), nv.getMaterial(values[j]));
			}
		}
		readBlockEntities();
	}

	private void read1_16() {
		boolean fullChunk = serializer.readBoolean();
		serializer.readBoolean(); // ignore old light
		int primaryBitmask = serializer.readVarInt();
		this.heightmaps = serializer.readNBTTag();

		
		if (fullChunk) {
			int[] biomeData = new int[1024];
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
		readBlockEntities();
	}

	private void read1_13() {

	}

	private void read1_9() {

	}

	public void read1_8() {
		boolean fullChunk = serializer.readBoolean();
		int bitmask = serializer.readUnsignedShort();
		int dataLength = serializer.readVarInt();
		byte[] data = new byte[dataLength];
		serializer.readBytes(data);
		if (fullChunk && bitmask == 0)
			return;
		deserializer1_8(true, data, bitmask, fullChunk);
	}
	
	public void deserializer1_8(boolean skyLight, byte[] data, int bitmask, boolean fullChunk) {
		PacketSerializer input = new PacketSerializer(serializer.getPlayer(), data);

		NamedVersion nv = version.getNamedVersion();
		// Read blocks
		for (int i = 0; i < 16; i++) {
			if ((bitmask & 1 << i) == 0)
				continue;
			ChunkSection section = ChunkSectionReader1_8.read(input, version);
			int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
			for (int j = 0; j < values.length; j++) {
				int x = j % 16, y = j / 256, z = j / 16 % 16;
				blocks.put(new BlockPosition(x + (chunkX * 16), y + (i * 16), z + (chunkZ * 16)), nv.getMaterial(values[j]));
			}
		}

		// Read block light

		/*for (int i = 0; i < sections.length; i++) {
			if ((bitmask & 1 << i) == 0)
				continue;
			sections[i].getLight().readBlockLight(input.getBuf());
		}

		// Read sky light
		if (skyLight) {
			for (int i = 0; i < sections.length; i++) {
				if ((bitmask & 1 << i) == 0)
					continue;
				sections[i].getLight().readSkyLight(input.getBuf());
			}
		}

		// Read biome data
		if (fullChunk) {
			for (int i = 0; i < 256; i++) {
				input.readUnsignedByte();
			}
		}
		input.release();*/

	}
}
