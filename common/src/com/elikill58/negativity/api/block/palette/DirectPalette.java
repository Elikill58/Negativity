package com.elikill58.negativity.api.block.palette;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public class DirectPalette implements Palette {

	private NamedVersion nv;
	
    public Material getStateForId(int id) {
        return nv.getMaterial(id);
    }

    public byte getBitsPerBlock() {
        return 15;
    }

    public void read(PacketSerializer data, NamedVersion nv) {
    	this.nv = nv;
        // No Data
    }
}
