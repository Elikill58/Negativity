package com.elikill58.negativity.api.item;

public enum Enchantment {

	DIG_SPEED("minecraft:efficiency"),
	DEPTH_STRIDER("minecraft:depth_strider"),
	THORNS("minecraft:thorns"),
	UNBREAKING("minecraft:unbreaking"),
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
	
	public static Enchantment getByName(String name) {
		for(Enchantment e : values())
			if(e.id.equalsIgnoreCase(name) || e.name().equalsIgnoreCase(name))
				return e;
		return null;
	}
}
