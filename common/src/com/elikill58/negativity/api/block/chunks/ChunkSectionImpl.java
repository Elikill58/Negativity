package com.elikill58.negativity.api.block.chunks;

import java.util.HashMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.palette.Palette;
import com.elikill58.negativity.api.block.palette.PaletteImpl;
import com.elikill58.negativity.api.block.palette.PaletteType;

public class ChunkSectionImpl implements ChunkSection {

	private HashMap<PaletteType, Palette> palettes = new HashMap<>();
    private ChunkSectionLight light;
    private int nonAirBlocksCount;

    public ChunkSectionImpl() {
    }

    public ChunkSectionImpl(final boolean holdsLight) {
    	setPalette(PaletteType.BLOCKS, new PaletteImpl(ChunkSection.SIZE));
        if (holdsLight) {
            this.light = new ChunkSectionLight();
        }
    }

    public ChunkSectionImpl(final boolean holdsLight, final int expectedPaletteLength) {
    	setPalette(PaletteType.BLOCKS, new PaletteImpl(ChunkSection.SIZE, expectedPaletteLength));
    	if (holdsLight) {
            this.light = new ChunkSectionLight();
        }
    }

    @Override
    public int getNonAirBlocksCount() {
        return nonAirBlocksCount;
    }

    @Override
    public void setNonAirBlocksCount(final int nonAirBlocksCount) {
        this.nonAirBlocksCount = nonAirBlocksCount;
    }

    @Override
    public @Nullable ChunkSectionLight getLight() {
        return light;
    }

    @Override
    public void setLight(@Nullable final ChunkSectionLight light) {
        this.light = light;
    }

	@Override
	public @Nullable Palette getPalette(PaletteType type) {
		return palettes.get(type);
	}

	@Override
	public void setPalette(PaletteType type, Palette blockPalette) {
		palettes.put(type, blockPalette);
	}

    

}
