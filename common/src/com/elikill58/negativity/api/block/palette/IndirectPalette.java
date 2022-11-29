package com.elikill58.negativity.api.block.palette;

import java.util.HashMap;
import java.util.Map;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public class IndirectPalette implements Palette {
	
    private Map<Integer, Material> idToState = new HashMap<>();
    private byte bitsPerBlock;

    public IndirectPalette(byte palBitsPerBlock) {
        bitsPerBlock = palBitsPerBlock;
    }

    public Material getStateForId(int id) {
        return idToState.get(id);
    }

    public byte getBitsPerBlock() {
        return bitsPerBlock;
    }

    public void read(PacketSerializer data, NamedVersion nv) {
        // Palette Length
        int length = data.readVarInt();
        // Palette
        for (int id = 0; id < length; id++) {
            int stateId = data.readVarInt();
            idToState.put(id, nv.getMaterial(stateId));
        }
    }
}
