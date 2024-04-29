/*
 * This file is part of ViaVersion - https://github.com/ViaVersion/ViaVersion
 * Copyright (C) 2016-2022 ViaVersion and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.elikill58.negativity.api.block.data.reader;

import com.elikill58.negativity.api.block.chunks.ChunkSection;
import com.elikill58.negativity.api.block.chunks.ChunkSectionImpl;
import com.elikill58.negativity.api.block.palette.PaletteType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

/**
 * Source: https://github.com/ViaVersion/ViaVersion/blob/master/api/src/main/java/com/viaversion/viaversion/api/type/types/version/ChunkSectionType1_16.java
 */
public class ChunkSectionReader1_16 {
	
	private static final int GLOBAL_PALETTE = 15;

	public static ChunkSection read(PacketSerializer serializer, Version version) {
        // Read bits per block
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
            	ChunkSectionReaderUtils.iterateCompactArrayWithPadding(bitsPerBlock, ChunkSection.SIZE, blockData,
                        bitsPerBlock == GLOBAL_PALETTE ? chunkSection::setFlatBlock : chunkSection::setPaletteIndex);
            }
        }

        return chunkSection;
	}

}
