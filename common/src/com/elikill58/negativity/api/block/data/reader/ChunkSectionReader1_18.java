package com.elikill58.negativity.api.block.data.reader;

import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.chunks.ChunkSectionImpl;
import com.elikill58.negativity.api.block.palette.Palette;
import com.elikill58.negativity.api.block.palette.PaletteImpl;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

/**
 * Source: https://github.com/ViaVersion/ViaVersion/blob/master/api/src/main/java/com/viaversion/viaversion/api/type/types/version/ChunkSectionType1_18.java
 */
public class ChunkSectionReader1_18 implements ChunkSectionReader {

	private static final int GLOBAL_PALETTE_BITS = 15;
	
	@Override
	public ChunkSection read(PacketSerializer serializer, Version version) {
        final ChunkSection chunkSection = new ChunkSectionImpl();
        chunkSection.setNonAirBlocksCount(serializer.readShort());
        chunkSection.setPalette(PaletteType.BLOCKS, readPalette(PaletteType.BLOCKS, serializer, version));
        chunkSection.setPalette(PaletteType.BIOMES, readPalette(PaletteType.BIOMES, serializer, version));
        return chunkSection;
	}

    public Palette readPalette(PaletteType type, PacketSerializer serializer, Version version) {
    	final int originalBitsPerValue = serializer.readByte();
        int bitsPerValue = originalBitsPerValue;

        PaletteImpl palette;
        if (bitsPerValue == 0) {
            // Single value storage
            palette = new PaletteImpl(type.size(), 1);
            palette.addId(serializer.readVarInt());
            serializer.readLongArray(); // Just eat it if not empty - thanks, Hypixel
            return palette;
        }

        if (bitsPerValue < 0 || bitsPerValue > type.highestBitsPerValue()) {
            bitsPerValue = GLOBAL_PALETTE_BITS;
        } else if (type == PaletteType.BLOCKS && bitsPerValue < 4) {
            bitsPerValue = 4; // Linear block palette values are always 4 bits
        }

        // Read palette
        if (bitsPerValue != GLOBAL_PALETTE_BITS) {
            final int paletteLength = serializer.readVarInt();
            palette = new PaletteImpl(type.size(), paletteLength);
            for (int i = 0; i < paletteLength; i++) {
                palette.addId(serializer.readVarInt());
            }
        } else {
            palette = new PaletteImpl(type.size());
        }

        // Read values
        final long[] values = serializer.readLongArray();
        if (values.length > 0) {
            final int valuesPerLong = (char) (64 / bitsPerValue);
            final int expectedLength = (type.size() + valuesPerLong - 1) / valuesPerLong;
            if (values.length == expectedLength) { // Thanks, Hypixel
            	ChunkSectionReader.iterateCompactArrayWithPadding(bitsPerValue, type.size(), values,
                        bitsPerValue == GLOBAL_PALETTE_BITS ? palette::setIdAt : palette::setPaletteIndexAt);
            }
        }
        return palette;
    }

}
