package com.elikill58.negativity.api.block.chunks;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.palette.Palette;
import com.elikill58.negativity.api.block.palette.PaletteType;

public interface ChunkSection {

	/**
	 * Size (dimensions) of blocks in a chunks section.
	 */
	int SIZE = 16 * 16 * 16; // width * depth * height

	/**
	 * Size (dimensions) of biomes in a chunks section.
	 */
	int BIOME_SIZE = 4 * 4 * 4;

	static int index(int x, int y, int z) {
		return y << 8 | z << 4 | x;
	}

	static int xFromIndex(int idx) {
		return idx & 0xF;
	}

	static int yFromIndex(int idx) {
		return idx >> 8 & 0xF;
	}

	static int zFromIndex(int idx) {
		return idx >> 4 & 0xF;
	}

	/**
	 * Returns the number of non-air blocks in this section.
	 *
	 * @return non-air blocks in this section
	 */
	int getNonAirBlocksCount();

	void setNonAirBlocksCount(int nonAirBlocksCount);

	/**
	 * Returns whether this section holds light data. Only true for &lt; 1.14
	 * chunks.
	 *
	 * @return whether this section holds light data
	 */
	default boolean hasLight() {
		return getLight() != null;
	}

	/**
	 * Returns the light of the chunk section. Only present for &lt; 1.14 chunks,
	 * otherwise sent separately.
	 *
	 * @return chunk section light if present
	 */
	@Nullable
	ChunkSectionLight getLight();

	void setLight(@Nullable ChunkSectionLight light);

	@Nullable
	Palette getPalette(PaletteType type);

	void setPalette(PaletteType type, Palette blockPalette);

	default void setFlatBlock(int idx, int id) {
		getPalette(PaletteType.BLOCKS).setIdAt(idx, id);
	}
	
    default void setPaletteIndex(int idx, int index) {
       getPalette(PaletteType.BLOCKS).setPaletteIndexAt(idx, index);
    }
    
    default void addPaletteEntry(PaletteType type, int id) {
    	getPalette(type).addId(id);
    }
}
