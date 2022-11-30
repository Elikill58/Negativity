package com.elikill58.negativity.api.block.data;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_16;
import com.elikill58.negativity.api.block.data.reader.ChunkSectionReader1_18;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

public class ChunkData {

	public int chunkX, chunkZ;
	/**
	 * Appear with chunk remap (since 1.16)
	 */
	public Object heightmaps;
	public HashMap<BlockPosition, Material> blocks = new HashMap<>();
	public HashMap<BlockPosition, Material> blockEntites = new HashMap<>();

	public ChunkData(PacketSerializer serializer, Version version) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		NamedVersion nv = version.getOrCreateNamedVersion();
		if(version.isNewerOrEquals(Version.V1_18)) {
			this.heightmaps = serializer.readNBTTag();
			ChunkSectionReader reader = new ChunkSectionReader1_18();
			PacketSerializer sectionsBuf = new PacketSerializer(serializer.readBytes(serializer.readVarInt()));
            for (int i = 0; i < 16; i++) {
            	ChunkSection section = reader.read(sectionsBuf, version);
            	int[] values = section.getPalette(PaletteType.BLOCKS).getValues();
            	for(int j = 0; j < values.length; j++) {
            		int x = j % 16,
            		    y = j / 256 + (i * 16),
            		    z = j / 16 % 16;
            		
            		blocks.put(new BlockPosition(x + (chunkX * 16), y, z + (chunkZ * 16)), nv.getMaterial(values[j]));
            	}
            }
		} else { // for 1.16 & 1.17
			// NOT TESTED - IN WIP
	        boolean fullChunk = serializer.readBoolean();
	        if(version.isNewerOrEquals(Version.V1_16))
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
			
			ChunkSectionReader reader = new ChunkSectionReader1_16();

	        for (int i = 0; i < 16; i++) {
	            if ((primaryBitmask & (1 << i)) == 0) continue; // Section not set

	            short nonAirBlocksCount = serializer.readShort();
	            ChunkSection section = reader.read(serializer, version);
	            section.setNonAirBlocksCount(nonAirBlocksCount);
	            //section.forBlocks(chunkX, i, chunkZ, (pos, id) -> blocks.put(pos, nv.getMaterial(id)));
	        }
		}
		
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
}
