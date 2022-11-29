package com.elikill58.negativity.api.block.palette;

import com.elikill58.negativity.api.block.chunks.ChunkSection;

public enum PaletteType {
	
    BLOCKS(ChunkSection.SIZE, 8),
    BIOMES(ChunkSection.BIOME_SIZE, 3);

    private final int size;
    private final int highestBitsPerValue;

    PaletteType(final int size, final int highestBitsPerValue) {
        this.size = size;
        this.highestBitsPerValue = highestBitsPerValue;
    }

    public int size() {
        return size;
    }

    public int highestBitsPerValue() {
        return highestBitsPerValue;
    }
}
