package com.elikill58.negativity.api.item;

public enum Enchantment {
	
	DIG_SPEED,
	THORNS,
	SOUL_SPEED,
	EFFICIENCY;
	
	@Override
	public String toString() {
		return "Enchantment:" + name();
	}
}
