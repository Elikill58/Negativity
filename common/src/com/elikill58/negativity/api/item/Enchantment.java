package com.elikill58.negativity.api.item;

public enum Enchantment {
	
	DIG_SPEED("minecraft:efficiency"),
	THORNS("minecraft:thorns"),
	SOUL_SPEED("minecraft:soul_speed");
	
	private final String id;
	
	Enchantment(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Enchantment:" + name();
	}
}
