package com.elikill58.negativity.api.potion;

public enum PotionEffectType {
	
	BLINDNESS,
	CONFUSION,
	DOLPHINS_GRACE,
	INSTANT_HEAL,
	FAST_DIGGING,
	HUNGER,
	JUMP,
	LEVITATION,
	NIGHT_VISION,
	POISON,
	REGENERATION,
	SLOW_DIGGING,
	SPEED,
	WEAKNESS,
	WITHER;
	
	public static PotionEffectType fromName(String name) {
		for(PotionEffectType type : values())
			if(type.name().equalsIgnoreCase(name))
				return type;
		return null;
	}
}
