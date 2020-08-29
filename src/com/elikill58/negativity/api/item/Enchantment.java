package com.elikill58.negativity.api.item;

public enum Enchantment {
	DIG_SPEED,
	THORNS,
	EFFICIENCY;
	
	@Override
	public String toString() {
		return "Enchantment:" + name();
	}
}
