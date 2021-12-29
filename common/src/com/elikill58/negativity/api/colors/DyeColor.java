package com.elikill58.negativity.api.colors;

import com.elikill58.negativity.api.item.Material;

public enum DyeColor {

	PURPLE((short) 10, (short) 5),
	GRAY((short) 7, (short) 8),
	ORANGE((short) 1, (short) 14),
	LIME((short) 5, (short) 10),
	LIGHT_BLUE((short) 3, (short) 12),
	MAGENTA((short) 2, (short) 13),
	PINK((short) 6, (short) 9),
	RED((short) 14, (short) 1),
	WHITE((short) 0, (short) 15),
	YELLOW((short) 4, (short) 11);
	
	private final short wool, dye;
	
	DyeColor(short wool, short dye) {
		this.wool = wool;
		this.dye = dye;
	}
	
	public short getWool() {
		return wool;
	}
	
	public short getDye() {
		return dye;
	}
	
	public short getColorFor(Material type) {
		if(type.getId().contains("WOOL") || type.getId().contains("STAINED_GLASS_PANE") || type.getId().contains("CARPET"))
			return getWool();
		else if(type.getId().equalsIgnoreCase("INK_SACK"))
			return getDye();
		return 0;
	}
}
