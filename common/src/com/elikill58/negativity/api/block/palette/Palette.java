package com.elikill58.negativity.api.block.palette;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

public interface Palette {

    Material getStateForId(int id);
    
    byte getBitsPerBlock();
    
    void read(PacketSerializer data, NamedVersion nv);


	public static Palette createPalette(byte bitsPerBlock) {
	    if (bitsPerBlock <= 4) {
	        return new IndirectPalette((byte) 4);
	    } else if (bitsPerBlock <= 8) {
	        return new IndirectPalette(bitsPerBlock);
	    } else {
	        return new DirectPalette();
	    }
	}
}
