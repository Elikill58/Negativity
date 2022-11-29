package com.elikill58.negativity.api.block.palette;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Source: https://github.com/ViaVersion/ViaVersion/blob/master/api/src/main/java/com/viaversion/viaversion/api/minecraft/chunks/DataPalette.java
 */
public class PaletteImpl implements Palette {

    private final List<Integer> palette;
    private final HashMap<Integer, Integer> inversePalette;
    private final int[] values;
    private final int sizeBits;

    public PaletteImpl(final int valuesLength) {
        this(valuesLength, 8);
    }

    public PaletteImpl(final int valuesLength, final int expectedPaletteLength) {
        this.values = new int[valuesLength];
        sizeBits = Integer.numberOfTrailingZeros(valuesLength) / 3;
        // Pre-size the palette array/map
        palette = new ArrayList<>(expectedPaletteLength);
        inversePalette = new HashMap<>(expectedPaletteLength);
    }

    @Override
    public int index(final int x, final int y, final int z) {
        return (y << this.sizeBits | z) << this.sizeBits | x;
    }

    @Override
    public int idAt(final int sectionCoordinate) {
        final int index = values[sectionCoordinate];
        return palette.get(index);
    }

    @Override
    public void setIdAt(final int sectionCoordinate, final int id) {
        int index = inversePalette.get(id);
        if (index == -1) {
            index = palette.size();
            palette.add(id);
            inversePalette.put(id, index);
        }

        values[sectionCoordinate] = index;
    }

    @Override
    public int paletteIndexAt(final int packedCoordinate) {
        return values[packedCoordinate];
    }

    @Override
    public void setPaletteIndexAt(final int sectionCoordinate, final int index) {
        values[sectionCoordinate] = index;
    }

    @Override
    public int size() {
        return palette.size();
    }

    @Override
    public int idByIndex(final int index) {
        return palette.get(index);
    }

    @Override
    public void setIdByIndex(final int index, final int id) {
        final int oldId = palette.set(index, id);
        if (oldId == id) return;

        inversePalette.put(id, index);
        if (inversePalette.getOrDefault(oldId, -1) == index) {
            inversePalette.remove(oldId);
            for (int i = 0; i < palette.size(); i++) {
                if (palette.get(i) == oldId) {
                    inversePalette.put(oldId, i);
                    break;
                }
            }
        }
    }

    @Override
    public void replaceId(final int oldId, final int newId) {
        final int index = inversePalette.remove(oldId);
        if (index == -1) return;

        inversePalette.put(newId, index);
        for (int i = 0; i < palette.size(); i++) {
            if (palette.get(i) == oldId) {
                palette.set(i, newId);
            }
        }
    }

    @Override
    public void addId(final int id) {
        inversePalette.put(id, palette.size());
        palette.add(id);
    }

    @Override
    public void clear() {
        palette.clear();
        inversePalette.clear();
    }
    
    @Override
    public int[] getValues() {
    	return values;
    }
}
