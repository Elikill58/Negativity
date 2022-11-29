package com.elikill58.negativity.api.block.data.reader;

import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.chunks.ChunkSectionImpl;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

/**
 * Source: https://github.com/ViaVersion/ViaVersion/blob/master/api/src/main/java/com/viaversion/viaversion/api/type/types/version/ChunkSectionType1_16.java
 */
public class ChunkSectionReader1_16 implements ChunkSectionReader {
	
	private static final int GLOBAL_PALETTE = 15;

	@Override
	public ChunkSection read(PacketSerializer serializer, Version version) {
        // Reaad bits per block
        int bitsPerBlock = serializer.readUnsignedByte();

        if (bitsPerBlock > 8) {
            bitsPerBlock = GLOBAL_PALETTE;
        } else if (bitsPerBlock < 4) {
            bitsPerBlock = 4;
        }

        // Read palette
        ChunkSection chunkSection;
        if (bitsPerBlock != GLOBAL_PALETTE) {
            int paletteLength = serializer.readVarInt();
            chunkSection = new ChunkSectionImpl(false, paletteLength);
            for (int i = 0; i < paletteLength; i++) {
                chunkSection.addPaletteEntry(PaletteType.BLOCKS, serializer.readVarInt());
            }
        } else {
            chunkSection = new ChunkSectionImpl(false);
        }

        // Read blocks
        long[] blockData = serializer.readLongArray();
        if (blockData.length > 0) {
            char valuesPerLong = (char) (64 / bitsPerBlock);
            int expectedLength = (ChunkSection.SIZE + valuesPerLong - 1) / valuesPerLong;
            if (blockData.length == expectedLength) {
            	ChunkSectionReader.iterateCompactArrayWithPadding(bitsPerBlock, ChunkSection.SIZE, blockData,
                        bitsPerBlock == GLOBAL_PALETTE ? chunkSection::setFlatBlock : chunkSection::setPaletteIndex);
            }
        }

        return chunkSection;
	}

}
