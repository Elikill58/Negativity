package com.elikill58.negativity.api.colors;

public enum DyeColor {
	
	GRAY((short) 7),
	LIME((short) 5),
	RED((short) 14),
	WHITE((short) 0),
	YELLOW((short) 4);
	
	private final short wool;
	
	private DyeColor(short wool) {
		this.wool = wool;
	}
	
	public short getWool() {
		return wool;
	}
}
