package com.elikill58.negativity.api.packets.nms.versions;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;

public class PreFlattening {

	public static Material get(int id) {
		// should we check for all blocks as described here: https://minecraft.fandom.com/wiki/Java_Edition_data_values/Pre-flattening#Block_IDs ?
		return Materials.AIR;
	}
}
